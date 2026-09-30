package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import java.util.Objects;

/**
 * One item in the cart being checked out; prices in cents.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public record CartItem(String sku, int quantity, long unitPriceCents) {

    public CartItem {
        Objects.requireNonNull(sku, "sku");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1: " + quantity);
        }
    }

    public long totalCents() {
        return quantity * unitPriceCents;
    }
}
