package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern;

import java.util.Objects;

/**
 * The same cart items as the classic Visitor, as a sealed hierarchy of records: no {@code accept}, no visitor
 * interface. Operations are exhaustive {@code switch} expressions in {@link CartOperations}.
 *
 * @see "m08 lesson, section Visitor — Modern Java 27"
 */
public sealed interface CartItem {

    record Book(String title, long priceCents) implements CartItem {
        public Book {
            Objects.requireNonNull(title, "title");
            requireNotNegative(priceCents);
        }
    }

    record Electronics(String name, long priceCents, int weightGrams) implements CartItem {
        public Electronics {
            Objects.requireNonNull(name, "name");
            requireNotNegative(priceCents);
            if (weightGrams <= 0) {
                throw new IllegalArgumentException("weight must be positive: " + weightGrams);
            }
        }
    }

    record Grocery(String name, long pricePerKgCents, int grams) implements CartItem {
        public Grocery {
            Objects.requireNonNull(name, "name");
            requireNotNegative(pricePerKgCents);
            if (grams <= 0) {
                throw new IllegalArgumentException("grams must be positive: " + grams);
            }
        }

        /** The line price, rounded half-up to the cent. */
        public long priceCents() {
            return (Math.multiplyExact(pricePerKgCents, grams) + 500) / 1000;
        }
    }

    private static void requireNotNegative(long cents) {
        if (cents < 0) {
            throw new IllegalArgumentException("price must not be negative: " + cents);
        }
    }
}
