package io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed;

import java.util.Objects;

/**
 * One order line: {@code quantity} units of {@code sku} at {@code unitPriceCents}, printed as {@code 2 x BOOK @ 350}.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public record Line(String sku, int quantity, long unitPriceCents) {

    public Line {
        Objects.requireNonNull(sku, "sku");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
        if (unitPriceCents <= 0) {
            throw new IllegalArgumentException("unit price must be positive: " + unitPriceCents);
        }
    }

    public long totalCents() {
        return Math.multiplyExact(unitPriceCents, quantity);
    }

    @Override
    public String toString() {
        return quantity + " x " + sku + " @ " + unitPriceCents;
    }
}
