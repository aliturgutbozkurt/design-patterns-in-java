package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic;

import java.util.Objects;

/**
 * Concrete element: groceries sold by weight.
 *
 * @see "m08 lesson, section Visitor — Classic Java"
 */
public record Grocery(String name, long pricePerKgCents, int grams) implements CartItem {

    public Grocery {
        Objects.requireNonNull(name, "name");
        if (pricePerKgCents < 0) {
            throw new IllegalArgumentException("price must not be negative: " + pricePerKgCents);
        }
        if (grams <= 0) {
            throw new IllegalArgumentException("grams must be positive: " + grams);
        }
    }

    /** The line price, rounded half-up to the cent. */
    public long priceCents() {
        return (Math.multiplyExact(pricePerKgCents, grams) + 500) / 1000;
    }

    @Override
    public <R> R accept(CartVisitor<R> visitor) {
        return visitor.visitGrocery(this);
    }
}
