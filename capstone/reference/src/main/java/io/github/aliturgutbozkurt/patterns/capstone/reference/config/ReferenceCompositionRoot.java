package io.github.aliturgutbozkurt.patterns.capstone.reference.config;

import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShop;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.api.ShopEnvironment;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.in.cli.CliAdapter;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryCartRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryOrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryProductRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryPromotionRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.notification.GatewayNotifier;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.payment.ExternalPaymentAdapter;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.warehouse.WarehouseAdapter;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.CartService;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.CatalogueService;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.CheckoutService;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.FulfilmentService;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.Inventory;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.OrderService;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.PricingService;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.ReportService;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.EventDispatcher;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.UnitOfWork;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.notify.CustomerNotifier;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.notify.StockAlerts;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.ids.SequentialIds;
import java.util.Objects;

/**
 * The composition root: the only place that knows every concrete class. It creates one object graph per shop (no
 * static state), wires the services to the outbound adapters and the environment, subscribes the observers, and
 * returns the inbound ports.
 *
 * @see "capstone guide, Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.DEPENDENCY_INJECTION, role = "composition root")
public final class ReferenceCompositionRoot implements PatternShopFactory {

    /** The outbound adapters and the event infrastructure of one shop. */
    private record Infrastructure(InMemoryProductRepository products, InMemoryCartRepository carts,
                                  InMemoryOrderRepository orders, InMemoryPromotionRepository promotions,
                                  ExternalPaymentAdapter payments, EventDispatcher dispatcher, UnitOfWork unitOfWork) {
    }

    @Override
    public PatternShop create(ShopEnvironment env) {
        Objects.requireNonNull(env, "env");
        var dispatcher = new EventDispatcher(env.errors());
        var infra = new Infrastructure(new InMemoryProductRepository(), new InMemoryCartRepository(),
                new InMemoryOrderRepository(), new InMemoryPromotionRepository(),
                new ExternalPaymentAdapter(env.payments(), env.settings().merchantId()), dispatcher,
                new UnitOfWork(dispatcher));
        var notifier = new GatewayNotifier(env.notifications());
        new CustomerNotifier(infra.orders(), notifier).subscribeTo(dispatcher);
        new StockAlerts(notifier).subscribeTo(dispatcher);
        return services(env, infra);
    }

    private static PatternShop services(ShopEnvironment env, Infrastructure infra) {
        var inventory = new Inventory(infra.products(), env.settings().lowStockThreshold());
        var pricing = new PricingService(infra.promotions(), infra.carts(), infra.products(), env.clock());
        var catalogue = new CatalogueService(infra.products(), infra.unitOfWork());
        var carts = new CartService(infra.carts(), infra.products(), infra.promotions(), SequentialIds.forCarts(),
                env.clock(), infra.unitOfWork());
        var checkout = new CheckoutService(infra.carts(), infra.orders(), pricing, infra.payments(), inventory,
                SequentialIds.forOrders(), env.clock(), infra.unitOfWork());
        var orders = new OrderService(infra.orders(), infra.payments(), inventory, env.clock(), infra.unitOfWork());
        var fulfilment = new FulfilmentService(infra.orders(), new WarehouseAdapter(env.warehouse()), env.clock(),
                infra.unitOfWork(), env.settings().maxParallelOrders());
        var reports = new ReportService(infra.orders(), infra.products(), env.clock(),
                env.settings().lowStockThreshold());
        return new ReferenceShop(catalogue, carts, pricing, checkout, orders, infra.dispatcher(), fulfilment, reports,
                new CliAdapter(catalogue, carts, pricing, checkout, orders, fulfilment, reports));
    }
}
