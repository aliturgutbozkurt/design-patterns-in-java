package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern;

import java.util.Objects;

/**
 * One line of an order; prices are in cents.
 *
 * @see "m09 lesson, section Data-oriented programming — Modern Java 27"
 */
public record OrderLine(String sku, int quantity, long unitPriceCents) {

    public OrderLine {
        Objects.requireNonNull(sku, "sku");
        if (sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1: " + quantity);
        }
        if (unitPriceCents < 0) {
            throw new IllegalArgumentException("unitPriceCents must not be negative: " + unitPriceCents);
        }
    }

    public long totalCents() {
        return quantity * unitPriceCents;
    }
}
