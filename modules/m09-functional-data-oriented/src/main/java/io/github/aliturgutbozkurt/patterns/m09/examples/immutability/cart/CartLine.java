package io.github.aliturgutbozkurt.patterns.m09.examples.immutability.cart;

import java.util.Objects;

/**
 * One line of a shopping cart; every component is itself immutable, so the record is too.
 *
 * @see "m09 lesson, section Immutability and value objects"
 */
public record CartLine(String sku, int quantity, long unitPriceCents) {

    public CartLine {
        Objects.requireNonNull(sku, "sku");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1: " + quantity);
        }
        if (unitPriceCents < 0) {
            throw new IllegalArgumentException("unitPriceCents must not be negative: " + unitPriceCents);
        }
    }

    public CartLine withQuantity(int newQuantity) {
        return new CartLine(sku, newQuantity, unitPriceCents);
    }

    public long totalCents() {
        return quantity * unitPriceCents;
    }

    @Override
    public String toString() {
        return sku + " x" + quantity;
    }
}
