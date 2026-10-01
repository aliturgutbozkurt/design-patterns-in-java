package io.github.aliturgutbozkurt.patterns.m11.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. What the {@link OrderStore} keeps per order: state only, never events. */
public record OrderSnapshot(OrderId id, OrderStatus status, long totalCents) {

    public OrderSnapshot {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(status, "status");
    }
}
