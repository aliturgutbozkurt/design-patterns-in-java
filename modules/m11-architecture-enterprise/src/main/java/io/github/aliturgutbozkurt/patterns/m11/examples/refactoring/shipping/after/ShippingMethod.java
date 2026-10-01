package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after;

/**
 * After the refactoring: the type code became a closed set of records, each carrying only the data it needs. There
 * is no "unknown method" any more — the compiler knows every case.
 *
 * @see "m11 lesson, section Refactoring to patterns — replace type code with a sealed type"
 */
public sealed interface ShippingMethod {

    record Standard(int weightGrams, int distanceKm) implements ShippingMethod {
        public Standard {
            requireNonNegative(weightGrams, distanceKm);
        }
    }

    record Express(int weightGrams, int distanceKm) implements ShippingMethod {
        public Express {
            requireNonNegative(weightGrams, distanceKm);
        }
    }

    record Pickup() implements ShippingMethod {}

    private static void requireNonNegative(int weightGrams, int distanceKm) {
        if (weightGrams < 0 || distanceKm < 0) {
            throw new IllegalArgumentException("weight and distance must not be negative");
        }
    }
}
