package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.before;

/**
 * Before the refactoring: a {@code switch} on a {@code String} type code with nested {@code if}s. A typo in the code
 * compiles, runs, and ships for free.
 *
 * @see "m11 lesson, section Refactoring to patterns — replace type code with a sealed type"
 */
public final class ShippingCalculator {

    public long costCents(String method, int weightGrams, int distanceKm) {
        switch (method) {
            case "STANDARD": {
                long cost = 499;
                if (weightGrams > 2000) {
                    cost += 100L * ((weightGrams - 2000 + 999) / 1000);
                }
                if (distanceKm > 500) {
                    cost += 300;
                }
                return cost;
            }
            case "EXPRESS": {
                long cost = 999;
                if (weightGrams > 1000) {
                    cost += 200L * ((weightGrams - 1000 + 999) / 1000);
                }
                if (distanceKm > 500) {
                    cost *= 2;
                }
                return cost;
            }
            case "PICKUP":
                return 0;
            default:
                return 0; // unknown code: silently free shipping
        }
    }
}
