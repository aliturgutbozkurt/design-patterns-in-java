package io.github.aliturgutbozkurt.patterns.m09.examples.composition.pricing;

import java.util.Locale;

/**
 * A non-negative price in cents.
 *
 * @see "m09 lesson, section Function composition, currying and partial application"
 */
public record Price(long cents) {

    public Price {
        if (cents < 0) {
            throw new IllegalArgumentException("price must not be negative: " + cents);
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%d.%02d", cents / 100, cents % 100);
    }
}
