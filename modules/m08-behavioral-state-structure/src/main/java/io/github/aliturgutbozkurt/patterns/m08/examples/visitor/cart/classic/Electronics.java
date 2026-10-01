package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic;

import java.util.Objects;

/**
 * Concrete element: an electronic device with its shipping weight.
 *
 * @see "m08 lesson, section Visitor — Classic Java"
 */
public record Electronics(String name, long priceCents, int weightGrams) implements CartItem {

    public Electronics {
        Objects.requireNonNull(name, "name");
        if (priceCents < 0) {
            throw new IllegalArgumentException("price must not be negative: " + priceCents);
        }
        if (weightGrams <= 0) {
            throw new IllegalArgumentException("weight must be positive: " + weightGrams);
        }
    }

    @Override
    public <R> R accept(CartVisitor<R> visitor) {
        return visitor.visitElectronics(this);
    }
}
