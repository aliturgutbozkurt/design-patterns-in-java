package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. A catalogue entry with its unit price and the units in stock. */
public record Product(Sku sku, String name, Money price, int stock) {

    public Product {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(price, "price");
    }
}
