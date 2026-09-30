package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain;

import java.util.Objects;

/**
 * Product code such as {@code BOOK-1}.
 *
 * @param value upper-case letters, a dash and digits
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public record Sku(String value) {

    public Sku {
        Objects.requireNonNull(value, "value");
        if (!value.matches("[A-Z]+-\\d+")) {
            throw new IllegalArgumentException("invalid SKU: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
