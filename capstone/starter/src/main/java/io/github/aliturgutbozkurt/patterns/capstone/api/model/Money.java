package io.github.aliturgutbozkurt.patterns.capstone.api.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * GIVEN — do not modify. A non-negative amount of Turkish lira (VAT included) in whole kuruş; prints as {@code 987.91}.
 *
 * @param kurus the amount in kuruş (1/100 lira), never negative
 * @see "capstone brief §2.2 — Money and ids"
 */
public record Money(long kurus) implements Comparable<Money> {

    /** {@code 0.00} */
    public static final Money ZERO = new Money(0);

    public Money {
        if (kurus < 0) {
            throw new IllegalArgumentException("negative amount: " + kurus);
        }
    }

    /** Parses a non-negative decimal with at most two fraction digits, e.g. {@code "987.91"} or {@code "250"}. */
    public static Money of(String decimal) {
        Objects.requireNonNull(decimal, "decimal");
        try {
            return new Money(new BigDecimal(decimal.strip()).movePointRight(2).longValueExact());
        } catch (NumberFormatException | ArithmeticException e) {
            throw new IllegalArgumentException("not a money amount: " + decimal, e);
        }
    }

    /** {@code this + other}. */
    public Money plus(Money other) {
        return new Money(Math.addExact(kurus, other.kurus));
    }

    /** {@code this - other}; throws {@link IllegalArgumentException} if the result would be negative. */
    public Money minus(Money other) {
        if (other.kurus > kurus) {
            throw new IllegalArgumentException("negative result: " + toPlainString() + " - " + other.toPlainString());
        }
        return new Money(kurus - other.kurus);
    }

    /** {@code this × factor} for a non-negative factor. */
    public Money times(int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("negative factor: " + factor);
        }
        return new Money(Math.multiplyExact(kurus, factor));
    }

    /**
     * {@code percent}% of this amount (0–100), rounded {@link RoundingMode#HALF_EVEN HALF_EVEN} to the kuruş, like
     * all money in the course: 5% of 1039.91 (51.9955) is 52.00, 10% of 0.05 (0.005) is 0.00 and 10% of 0.15
     * (0.015) is 0.02.
     */
    public Money percent(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent out of range: " + percent);
        }
        return new Money(BigDecimal.valueOf(Math.multiplyExact(kurus, percent), 2)
                .setScale(0, RoundingMode.HALF_EVEN)
                .longValueExact());
    }

    /** The smaller of the two amounts. */
    public Money min(Money other) {
        return compareTo(other) <= 0 ? this : other;
    }

    /** Whether this is {@code 0.00}. */
    public boolean isZero() {
        return kurus == 0;
    }

    @Override
    public int compareTo(Money other) {
        return Long.compare(kurus, other.kurus);
    }

    /** Two fraction digits, no grouping, no currency: {@code 987.91}, {@code 0.00}. */
    public String toPlainString() {
        return BigDecimal.valueOf(kurus, 2).toPlainString();
    }
}
