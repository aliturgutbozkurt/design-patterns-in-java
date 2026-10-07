package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * One line of a cart.
 *
 * @param sku      the product
 * @param quantity units, ≥ 1
 * @see "capstone guide, Slice walkthrough — C3"
 */
public record CartItem(Sku sku, int quantity) {

    public CartItem {
        Objects.requireNonNull(sku, "sku");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
    }
}
