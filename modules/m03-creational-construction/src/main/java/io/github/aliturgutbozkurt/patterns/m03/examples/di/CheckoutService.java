package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Objects;

/**
 * Business logic that creates nothing itself: every collaborator — even the clock — arrives in the constructor.
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
public final class CheckoutService {

    private final PriceCalculator prices;
    private final PaymentGateway payments;
    private final OrderRepository orders;
    private final Clock clock;

    public CheckoutService(PriceCalculator prices, PaymentGateway payments, OrderRepository orders, Clock clock) {
        this.prices = Objects.requireNonNull(prices, "prices");
        this.payments = Objects.requireNonNull(payments, "payments");
        this.orders = Objects.requireNonNull(orders, "orders");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /** Prices, charges and stores the order; an unknown item fails before anything is charged. */
    public OrderRecord checkout(String customer, String item, int quantity) {
        BigDecimal total = prices.priceOf(item, quantity);
        String receipt = payments.charge(customer, total);
        var order = new OrderRecord(customer, item, quantity, total, receipt, clock.instant());
        orders.save(order);
        return order;
    }
}
