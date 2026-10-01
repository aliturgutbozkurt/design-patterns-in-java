package io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog;

import java.math.BigDecimal;

/**
 * A non-negative amount in cents; prints with two decimals ({@code 39.90}).
 *
 * @param cents the amount in cents, never negative
 * @see "m11 lesson, section Repository"
 */
public record Money(long cents) implements Comparable<Money> {

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("negative amount: " + cents);
        }
    }

    /** Parses a decimal such as {@code "39.90"}; more than two decimals are rejected. */
    public static Money of(String decimal) {
        try {
            return new Money(new BigDecimal(decimal).movePointRight(2).longValueExact());
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("not a money amount: " + decimal, e);
        }
    }

    @Override
    public int compareTo(Money other) {
        return Long.compare(cents, other.cents);
    }

    @Override
    public String toString() {
        return BigDecimal.valueOf(cents, 2).toPlainString();
    }
}
