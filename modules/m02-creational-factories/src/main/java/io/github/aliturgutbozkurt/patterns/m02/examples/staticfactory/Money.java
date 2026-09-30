package io.github.aliturgutbozkurt.patterns.m02.examples.staticfactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Static factory methods with names that say what they do: {@code of}, {@code zero}, {@code parse}. A record's
 * constructor must stay public, so the factories sit next to it as the friendlier way in.
 *
 * @see "m02 lesson, section Static Factory Method"
 */
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + amount);
        }
        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
    }

    /** {@code Money.of("12.50", "EUR")}. */
    public static Money of(String amount, String currencyCode) {
        return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    /** Zero in the given currency. */
    public static Money zero(String currencyCode) {
        return of("0", currencyCode);
    }

    /** Parses {@code "<amount> <currency>"}, e.g. {@code "12.50 EUR"}. */
    public static Money parse(String text) {
        String[] parts = text.strip().split(" ");
        try {
            if (parts.length == 2) {
                return of(parts[0], parts[1]);
            }
        } catch (IllegalArgumentException e) {  // NumberFormatException is an IllegalArgumentException
            throw new IllegalArgumentException(parseError(text), e);
        }
        throw new IllegalArgumentException(parseError(text));
    }

    private static String parseError(String text) {
        return "cannot parse money: '" + text + "' (expected '<amount> <currency>')";
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency.getCurrencyCode();
    }
}
