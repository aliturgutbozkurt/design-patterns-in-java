package io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order;

import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * An amount of money in minor units (cents). Immutable: arithmetic returns a new value.
 *
 * @see "m10 lesson, section Immutable Object"
 */
public record Money(long cents, Currency currency) {

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + cents);
        }
        Objects.requireNonNull(currency, "currency");
    }

    /** {@code Money.of(1250, "EUR")} is 12.50 EUR. */
    public static Money of(long cents, String currencyCode) {
        return new Money(cents, Currency.getInstance(currencyCode));
    }

    public Money plus(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("mixed currencies: " + currency + " and " + other.currency);
        }
        return new Money(Math.addExact(cents, other.cents), currency);
    }

    public Money times(int factor) {
        return new Money(Math.multiplyExact(cents, factor), currency);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%d.%02d %s", cents / 100, cents % 100, currency);
    }
}
