package io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Per-request collaborator: mutable state that belongs to one request and must never leak into the next one.
 *
 * @see "m11 lesson, section Dependency Injection — lifetimes"
 */
public final class Basket {

    private final PriceList prices;
    private final List<String> items = new ArrayList<>();

    public Basket(PriceList prices) {
        this.prices = Objects.requireNonNull(prices, "prices");
    }

    /** Adds {@code item}; unknown items are rejected before the basket changes. */
    public void add(String item) {
        prices.priceOf(item);
        items.add(item);
    }

    /** The items in the order they were added (an unmodifiable copy). */
    public List<String> items() {
        return List.copyOf(items);
    }

    /** The sum of the item prices. */
    public BigDecimal total() {
        return items.stream().map(prices::priceOf).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
}
