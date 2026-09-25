package io.github.aliturgutbozkurt.patterns.m00.examples.records;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A whole-number percentage between 0 and 100. The {@code String} constructor shows flexible constructor bodies
 * (JEP 513, final in Java 25): we may validate and parse <em>before</em> delegating with {@code this(...)}.
 */
public record Percentage(int value) {

    public Percentage {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("percentage must be between 0 and 100: " + value);
        }
    }

    /** Parses text such as {@code "15%"} (surrounding spaces allowed). */
    public Percentage(String text) {
        Objects.requireNonNull(text, "text");
        String trimmed = text.strip();
        if (!trimmed.matches("\\d{1,3}%")) {
            throw new IllegalArgumentException("not a percentage: " + text);
        }
        this(Integer.parseInt(trimmed.substring(0, trimmed.length() - 1)));
    }

    /** This percentage of {@code price}, e.g. 15 % of 20.00 EUR = 3.00 EUR. */
    public Money of(Money price) {
        BigDecimal share = price.amount().multiply(BigDecimal.valueOf(value)).movePointLeft(2);
        return new Money(share, price.currency());
    }
}
