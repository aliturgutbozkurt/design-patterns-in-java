package io.github.aliturgutbozkurt.patterns.m07.examples.observer.modern;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Event pushed to {@link Ticker} listeners: an immutable value carrying both the old and the new price.
 *
 * @see "m07 lesson, section Observer — Modern Java 27"
 */
public record PriceChange(String symbol, BigDecimal oldPrice, BigDecimal newPrice) {

    public PriceChange {
        Objects.requireNonNull(symbol, "symbol");
        Objects.requireNonNull(oldPrice, "oldPrice");
        Objects.requireNonNull(newPrice, "newPrice");
        if (symbol.isBlank()) {
            throw new IllegalArgumentException("symbol must not be blank");
        }
    }

    /** New minus old price, e.g. {@code 1.50} or {@code -2.50}. */
    public BigDecimal delta() {
        return newPrice.subtract(oldPrice);
    }
}
