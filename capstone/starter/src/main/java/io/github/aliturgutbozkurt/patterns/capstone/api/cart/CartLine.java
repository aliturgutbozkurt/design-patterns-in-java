package io.github.aliturgutbozkurt.patterns.capstone.api.cart;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * GIVEN — do not modify. One line of a cart.
 *
 * @param sku      the product
 * @param quantity units, at least 1
 * @see "capstone brief §2.2 — Cart (F2)"
 */
public record CartLine(Sku sku, int quantity) {

    public CartLine {
        Objects.requireNonNull(sku, "sku");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
    }
}
