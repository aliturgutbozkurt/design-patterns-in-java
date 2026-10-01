package io.github.aliturgutbozkurt.patterns.m09.examples.composition.pricing;

import java.util.function.Function;

/**
 * Pricing rules as plain functions {@code Price -> Price}. Where the Decorator pattern wraps objects in objects,
 * these rules are stacked with {@link Function#andThen}. All arithmetic is in whole cents, rounded down.
 *
 * @see "m09 lesson, section Function composition, currying and partial application"
 */
public final class PriceRules {

    private PriceRules() {}

    public static Function<Price, Price> percentOff(int percent) {
        requirePercent(percent);
        return price -> new Price(price.cents() * (100 - percent) / 100);
    }

    /** Subtracts a fixed amount, but never below zero. */
    public static Function<Price, Price> amountOff(long cents) {
        return price -> new Price(Math.max(0, price.cents() - cents));
    }

    public static Function<Price, Price> addTax(int percent) {
        requirePercent(percent);
        return price -> new Price(price.cents() * (100 + percent) / 100);
    }

    /** The price, or {@code floorCents} if the price is lower. */
    public static Function<Price, Price> floorAt(long floorCents) {
        return price -> price.cents() < floorCents ? new Price(floorCents) : price;
    }

    private static void requirePercent(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be 0..100: " + percent);
        }
    }
}
