package io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus;

import java.util.Objects;

/**
 * Charging the customer for an order failed.
 *
 * @see "m07 lesson, section Observer — typed event bus"
 */
public record PaymentFailed(String orderId, String reason) implements ShopEvent {

    public PaymentFailed {
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(reason, "reason");
    }
}
