package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.List;
import java.util.Objects;

/** GIVEN — do not modify. A confirmed order; its total must equal the sum of its lines. */
public record Order(OrderId id, String customer, List<OrderLine> lines, Money total) {

    public Order {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(customer, "customer");
        Objects.requireNonNull(total, "total");
        lines = List.copyOf(lines);
        Money sum = lines.stream().map(OrderLine::total).reduce(Money.ZERO, Money::plus);
        if (!sum.equals(total)) {
            throw new IllegalArgumentException("total " + total.cents() + " != sum of lines " + sum.cents());
        }
    }
}
