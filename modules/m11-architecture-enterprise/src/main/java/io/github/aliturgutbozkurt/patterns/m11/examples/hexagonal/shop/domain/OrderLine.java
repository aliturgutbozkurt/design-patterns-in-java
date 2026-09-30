package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain;

import java.util.Objects;

/**
 * One line of an {@link Order}: the unit price is copied in when the order is placed, so later price changes do not
 * rewrite history.
 *
 * @param sku product
 * @param quantity positive
 * @param unitPrice price per unit at the time of ordering
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public record OrderLine(Sku sku, int quantity, Money unitPrice) {

    public OrderLine {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(unitPrice, "unitPrice");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
    }

    /** Quantity × unit price. */
    public Money total() {
        return unitPrice.times(quantity);
    }
}
