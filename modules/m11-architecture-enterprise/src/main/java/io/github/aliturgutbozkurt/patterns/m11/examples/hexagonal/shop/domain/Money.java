package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain;

import java.math.BigDecimal;

/**
 * A non-negative amount in cents; prints with two decimals ({@code 47.00}).
 *
 * @param cents the amount in cents, never negative
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public record Money(long cents) {

    public static final Money ZERO = new Money(0);

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("negative amount: " + cents);
        }
    }

    /** Parses a decimal such as {@code "20.00"}; more than two decimals are rejected. */
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

    public Money times(int factor) {
        return new Money(Math.multiplyExact(cents, factor));
    }

    @Override
    public String toString() {
        return BigDecimal.valueOf(cents, 2).toPlainString();
    }
}
