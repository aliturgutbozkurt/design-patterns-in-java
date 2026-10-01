package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.after;

import java.math.BigDecimal;

/**
 * A non-negative amount in cents: a negative amount cannot even be constructed.
 *
 * @param cents the amount in cents, never negative
 * @see "m11 lesson, section Anti-patterns — anaemic domain model"
 */
public record Money(long cents) implements Comparable<Money> {

    public static final Money ZERO = new Money(0);

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("negative amount: " + cents);
        }
    }

    public static Money of(String decimal) {
        return new Money(new BigDecimal(decimal).movePointRight(2).longValueExact());
    }

    public Money plus(Money other) {
        return new Money(Math.addExact(cents, other.cents));
    }

    public Money minus(Money other) {
        return new Money(cents - other.cents);
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
