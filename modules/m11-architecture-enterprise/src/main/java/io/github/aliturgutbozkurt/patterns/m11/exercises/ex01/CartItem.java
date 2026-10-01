package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. One entry of a shopping cart; the quantity is checked by the checkout, not here. */
public record CartItem(Sku sku, int quantity) {

    public CartItem {
        Objects.requireNonNull(sku, "sku");
    }
}
