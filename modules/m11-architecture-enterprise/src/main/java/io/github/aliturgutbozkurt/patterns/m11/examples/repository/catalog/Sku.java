package io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog;

import java.util.Objects;

/**
 * Stock-keeping unit such as {@code BOOK-1}: the identity of a {@link Product}, ordered by its text.
 *
 * @param value upper-case letters, a dash and digits
 * @see "m11 lesson, section Repository"
 */
public record Sku(String value) implements Comparable<Sku> {

    public Sku {
        Objects.requireNonNull(value, "value");
        if (!value.matches("[A-Z]+-\\d+")) {
            throw new IllegalArgumentException("invalid SKU: " + value);
        }
    }

    @Override
    public int compareTo(Sku other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
