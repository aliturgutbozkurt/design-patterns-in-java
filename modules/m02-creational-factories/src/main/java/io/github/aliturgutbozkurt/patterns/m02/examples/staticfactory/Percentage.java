package io.github.aliturgutbozkurt.patterns.m02.examples.staticfactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Instance caching: there are only 101 whole percentages, so {@link #of(int)} hands out shared instances — the same
 * idea as {@code Integer.valueOf} and {@code Boolean.valueOf}. A constructor could never do this: {@code new} always
 * creates a new object.
 *
 * @see "m02 lesson, section Static Factory Method"
 */
public final class Percentage {

    private static final Percentage[] CACHE = new Percentage[101];

    static {
        for (int i = 0; i < CACHE.length; i++) {
            CACHE[i] = new Percentage(i);
        }
    }

    private final int value;

    private Percentage(int value) {
        this.value = value;
    }

    /** The shared instance for {@code 0..100}; equal percentages are the same object. */
    public static Percentage of(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("percentage must be in 0..100: " + value);
        }
        return CACHE[value];
    }

    public int value() {
        return value;
    }

    /** This percentage of {@code amount}, scale 2, half-even. */
    public BigDecimal applyTo(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(value)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
    }

    @Override
    public String toString() {
        return value + "%";
    }
}
