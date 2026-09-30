package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Map;

/**
 * The composition root: the <em>only</em> place that calls {@code new} on collaborators and decides which
 * implementation each interface gets. Each collaborator is created once and shared — "singleton" by wiring, not by
 * a static field.
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
public final class CompositionRoot {

    private static final Map<String, BigDecimal> CATALOG = Map.of(
            "keyboard", new BigDecimal("49.90"),
            "mouse", new BigDecimal("19.99"),
            "monitor", new BigDecimal("229.00"));

    private CompositionRoot() {}

    /** Real wiring (the payment provider is simulated in this course). */
    public static ShopApp production() {
        return wire(new SimulatedPaymentGateway(), Clock.systemUTC());
    }

    /** Test wiring: the caller supplies a fake gateway and a fixed clock. */
    public static ShopApp forTests(PaymentGateway gateway, Clock clock) {
        return wire(gateway, clock);
    }

    private static ShopApp wire(PaymentGateway gateway, Clock clock) {
        var orders = new InMemoryOrderRepository();
        var checkout = new CheckoutService(new CatalogPriceCalculator(CATALOG), gateway, orders, clock);
        return new ShopApp(checkout, orders);
    }
}
