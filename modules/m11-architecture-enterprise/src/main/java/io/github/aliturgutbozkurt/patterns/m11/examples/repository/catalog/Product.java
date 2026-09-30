package io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog;

import java.util.Objects;

/**
 * A catalogue entry; immutable, so a repository can hand it out without copying.
 *
 * @param sku identity
 * @param name display name, not blank
 * @param category category
 * @param price unit price
 * @see "m11 lesson, section Repository"
 */
public record Product(Sku sku, String name, Category category, Money price) {

    public Product {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(price, "price");
        if (name.isBlank()) {
            throw new IllegalArgumentException("blank product name");
        }
    }
}
