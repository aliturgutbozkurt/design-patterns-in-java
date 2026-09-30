package io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A customer placed an order.
 *
 * @see "m07 lesson, section Observer — typed event bus"
 */
public record OrderPlaced(String orderId, String customer, BigDecimal total) implements ShopEvent {

    public OrderPlaced {
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(customer, "customer");
        Objects.requireNonNull(total, "total");
    }
}
