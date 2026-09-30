package io.github.aliturgutbozkurt.patterns.m08.examples.state.vending;

import java.util.Objects;

/**
 * What lands in the tray after a sale: the product and the change handed back with it.
 *
 * @see "m08 lesson, section State — Classic Java"
 */
public record Dispensed(Product product, int changeCents) {

    public Dispensed {
        Objects.requireNonNull(product, "product");
        if (changeCents < 0) {
            throw new IllegalArgumentException("change must not be negative: " + changeCents);
        }
    }

    @Override
    public String toString() {
        return product.name() + " (change " + changeCents + ")";
    }
}
