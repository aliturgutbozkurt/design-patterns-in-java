package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. Domain event published once per confirmed order, after it was saved. */
public record OrderPlaced(OrderId id, String customer, Money total) {

    public OrderPlaced {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(customer, "customer");
        Objects.requireNonNull(total, "total");
    }
}
