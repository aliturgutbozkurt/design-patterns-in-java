package io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders;

import java.util.Objects;

/**
 * One line of an {@link Order}.
 *
 * @param sku product code
 * @param quantity positive
 * @see "m11 lesson, section Repository — optimistic versioning"
 */
public record OrderLine(String sku, int quantity) {

    public OrderLine {
        Objects.requireNonNull(sku, "sku");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
    }

    @Override
    public String toString() {
        return sku + " x " + quantity;
    }
}
