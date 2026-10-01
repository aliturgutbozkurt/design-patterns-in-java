package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. One line of an {@link Order}: product, quantity and the unit price at checkout time. */
public record OrderLine(Sku sku, int quantity, Money unitPrice) {

    public OrderLine {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(unitPrice, "unitPrice");
    }

    /** Quantity × unit price. */
    public Money total() {
        return unitPrice.times(quantity);
    }
}
