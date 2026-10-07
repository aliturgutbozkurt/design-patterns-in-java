package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryCartRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryOrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryProductRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryPromotionRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.EventDispatcher;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.UnitOfWork;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.OrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentOutcome;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentPort;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.PhysicalProduct;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.ids.SequentialIds;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/** A small application wired by hand with hand-written doubles, for the application-service unit tests. */
final class ApplicationFixture {

    /** Payment double: approves or declines, and records every call. */
    static final class RecordingPayments implements PaymentPort {
        final List<String> calls = new ArrayList<>();
        PaymentOutcome refundOutcome = new PaymentOutcome.Approved("rfd-1");

        @Override
        public PaymentOutcome charge(Money amount, String cardToken, String attempt) {
            calls.add("charge " + amount.toPlainString());
            return new PaymentOutcome.Approved("txn-1");
        }

        @Override
        public PaymentOutcome refund(String paymentReference, Money amount) {
            calls.add("refund " + paymentReference + " " + amount.toPlainString());
            return refundOutcome;
        }
    }

    final Clock clock = Clock.fixed(Instant.parse("2026-11-16T06:00:00Z"), ZoneId.of("Europe/Istanbul"));
    final InMemoryProductRepository products = new InMemoryProductRepository();
    final InMemoryCartRepository carts = new InMemoryCartRepository();
    final InMemoryPromotionRepository promotions = new InMemoryPromotionRepository();
    final List<ShopEvent> events = new ArrayList<>();
    final EventDispatcher dispatcher = new EventDispatcher(e -> {
        throw new AssertionError(e);
    });
    final UnitOfWork unitOfWork = new UnitOfWork(dispatcher);
    final RecordingPayments payments = new RecordingPayments();
    final Inventory inventory = new Inventory(products, 5);
    final PricingService pricing = new PricingService(promotions, carts, products, clock);
    final CartService cartService = new CartService(carts, products, promotions, SequentialIds.forCarts(), clock,
            unitOfWork);

    ApplicationFixture() {
        products.save(new PhysicalProduct(new Sku("TOY-001"), "Puzzle", Category.TOYS, Money.of("120.00"), 6));
        dispatcher.subscribe(ShopEvent.class, events::add);
    }

    CheckoutService checkout(OrderRepository orders) {
        return checkout(orders, inventory);
    }

    CheckoutService checkout(OrderRepository orders, Inventory stock) {
        return new CheckoutService(carts, orders, pricing, payments, stock, SequentialIds.forOrders(), clock,
                unitOfWork);
    }

    OrderService orderService(InMemoryOrderRepository orders) {
        return new OrderService(orders, payments, inventory, clock, unitOfWork);
    }
}
