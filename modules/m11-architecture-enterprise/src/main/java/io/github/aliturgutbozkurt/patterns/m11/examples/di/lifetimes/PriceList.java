package io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Application-lifetime collaborator: immutable, so one instance can safely serve every request.
 *
 * @see "m11 lesson, section Dependency Injection — lifetimes"
 */
public final class PriceList {

    private final Map<String, BigDecimal> prices;

    public PriceList(Map<String, BigDecimal> prices) {
        this.prices = Map.copyOf(prices);
    }

    /** The price of {@code item}; unknown items are rejected. */
    public BigDecimal priceOf(String item) {
        BigDecimal price = prices.get(Objects.requireNonNull(item, "item"));
        if (price == null) {
            throw new IllegalArgumentException("unknown item: " + item);
        }
        return price;
    }
}
