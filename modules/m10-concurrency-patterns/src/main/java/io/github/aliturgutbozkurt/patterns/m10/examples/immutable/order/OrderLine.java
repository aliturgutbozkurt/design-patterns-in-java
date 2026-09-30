package io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order;

import java.util.Objects;

/**
 * One line of an order. The compact constructor is the only way in, so an invalid line cannot exist.
 *
 * @see "m10 lesson, section Immutable Object"
 */
public record OrderLine(String sku, int quantity, Money unitPrice) {

    public OrderLine {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
        Objects.requireNonNull(unitPrice, "unitPrice");
    }

    public Money total() {
        return unitPrice.times(quantity);
    }
}
