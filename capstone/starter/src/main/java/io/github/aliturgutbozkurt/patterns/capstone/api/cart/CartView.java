package io.github.aliturgutbozkurt.patterns.capstone.api.cart;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import java.util.List;
import java.util.Objects;

/**
 * GIVEN — do not modify. A snapshot of a cart.
 *
 * @param id       the cart
 * @param customer its owner
 * @param lines    the lines in the order they were first added
 * @param coupon   the applied coupon code, {@code ""} when none
 * @param open     {@code false} after a successful checkout
 * @see "capstone brief, Business rules — Cart (F2)"
 */
public record CartView(CartId id, CustomerId customer, List<CartLine> lines, String coupon, boolean open) {

    public CartView {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(customer, "customer");
        lines = List.copyOf(lines);
        Objects.requireNonNull(coupon, "coupon");
    }
}
