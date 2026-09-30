package io.github.aliturgutbozkurt.patterns.m08.examples.state.vending;

import java.util.Objects;

/**
 * A snack sold by the machine, priced in cents.
 *
 * @see "m08 lesson, section State — Classic Java"
 */
public record Product(String name, int priceCents) {

    public Product {
        Objects.requireNonNull(name, "name");
        if (priceCents <= 0) {
            throw new IllegalArgumentException("price must be positive: " + priceCents);
        }
    }
}
