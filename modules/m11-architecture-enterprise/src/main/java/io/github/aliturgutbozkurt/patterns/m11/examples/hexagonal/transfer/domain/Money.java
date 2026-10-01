package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain;

import java.math.BigDecimal;

/**
 * A non-negative amount in cents; prints with two decimals.
 *
 * @param cents the amount in cents, never negative
 * @see "m11 lesson, section Ports and Adapters"
 */
public record Money(long cents) implements Comparable<Money> {

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("negative amount: " + cents);
        }
    }

    /** Parses a decimal such as {@code "25.00"}; more than two decimals are rejected. */
    public static Money of(String decimal) {
        try {
            return new Money(new BigDecimal(decimal).movePointRight(2).longValueExact());
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("not a money amount: " + decimal, e);
        }
    }

    public Money plus(Money other) {
        return new Money(Math.addExact(cents, other.cents));
    }

    /** The difference; fails if it would be negative. */
    public Money minus(Money other) {
        return new Money(cents - other.cents);
    }

    public boolean isZero() {
        return cents == 0;
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
