package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain;

import java.util.Objects;

/**
 * What an {@link Order} can announce. A sealed hierarchy of immutable records, so subscribers can {@code switch}
 * over it exhaustively; the full lifecycle (paid, shipped, cancelled) is in the domain-events example.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public sealed interface OrderEvent {

    /** The order the event is about. */
    OrderId orderId();

    /** A customer placed an order. */
    record OrderPlaced(OrderId orderId, String customer, Money total) implements OrderEvent {
        public OrderPlaced {
            Objects.requireNonNull(orderId, "orderId");
            Objects.requireNonNull(customer, "customer");
            Objects.requireNonNull(total, "total");
        }
    }
}
