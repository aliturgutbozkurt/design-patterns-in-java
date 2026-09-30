package io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus;

import java.util.Objects;

/**
 * The warehouse shipped an order.
 *
 * @see "m07 lesson, section Observer — typed event bus"
 */
public record OrderShipped(String orderId, String trackingNumber) implements ShopEvent {

    public OrderShipped {
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(trackingNumber, "trackingNumber");
    }
}
