package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.config;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.inbound.cli.CommandLineAdapter;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.events.RecordingEventPublisher;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.file.FileOrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.memory.InMemoryOrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.memory.InMemoryProductCatalog;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.memory.SequentialOrderIds;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.payment.LegacyPaymentAdapter;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.payment.acme.AcmePaySandbox;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.PlaceOrderService;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderUseCase;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.OrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import java.nio.file.Path;
import java.util.Map;

/**
 * Composition root of the PatternShop hexagon — the only place that names adapter classes. Switching from memory to
 * files changes one argument here and nothing in the core.
 *
 * @param placeOrder the use case (inbound port)
 * @param cli the inbound adapter in front of it
 * @param orders the order repository adapter in use
 * @param events the event adapter (records what was published)
 * @param acme the payment provider sandbox behind the payment adapter
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public record ShopCompositionRoot(PlaceOrderUseCase placeOrder, CommandLineAdapter cli, OrderRepository orders,
                                  RecordingEventPublisher events, AcmePaySandbox acme) {

    private static final Map<Sku, Money> PRICES = Map.of(
            new Sku("BOOK-1"), Money.of("20.00"),
            new Sku("PEN-7"), Money.of("7.00"),
            new Sku("MUG-3"), Money.of("8.75"));
    private static final long CREDIT_LIMIT_CENTS = 500_00;

    /** Everything in memory: fast, for tests and demos. */
    public static ShopCompositionRoot inMemory() {
        return wire(new InMemoryOrderRepository());
    }

    /** Orders in {@code ordersFile}; ids continue after the orders already stored there. */
    public static ShopCompositionRoot fileBacked(Path ordersFile) {
        return wire(new FileOrderRepository(ordersFile));
    }

    private static ShopCompositionRoot wire(OrderRepository orders) {
        var acme = new AcmePaySandbox(CREDIT_LIMIT_CENTS);
        var events = new RecordingEventPublisher();
        var service = new PlaceOrderService(new InMemoryProductCatalog(PRICES), new LegacyPaymentAdapter(acme),
                orders, events, new SequentialOrderIds(orders.count()));
        return new ShopCompositionRoot(service, new CommandLineAdapter(service), orders, events, acme);
    }
}
