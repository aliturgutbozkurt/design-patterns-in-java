package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after;

import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod.Express;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod.Pickup;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod.Standard;

/**
 * The same prices as the old calculator, as an exhaustive {@code switch} with record patterns and no
 * {@code default}: adding a fourth method makes this class fail to compile until it is priced.
 *
 * @see "m11 lesson, section Refactoring to patterns — replace type code with a sealed type"
 */
public final class ShippingCalculator {

    public long costCents(ShippingMethod method) {
        return switch (method) {
            case Standard(int weight, int distance) -> 499 + 100 * startedKilosAbove(2000, weight)
                    + (distance > 500 ? 300 : 0);
            case Express(int weight, int distance) -> (999 + 200 * startedKilosAbove(1000, weight))
                    * (distance > 500 ? 2 : 1);
            case Pickup _ -> 0;
        };
    }

    /** Extracted method: every started kilogram above the free weight. */
    private static long startedKilosAbove(int freeGrams, int weightGrams) {
        return Math.max(0, (weightGrams - freeGrams + 999) / 1000);
    }
}
