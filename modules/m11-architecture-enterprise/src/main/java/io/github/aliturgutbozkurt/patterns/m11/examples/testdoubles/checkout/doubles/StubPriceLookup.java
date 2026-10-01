package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles;

import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.PriceLookup;
import java.util.Map;

/**
 * Stub: canned answers to queries, no logic, no verification.
 *
 * @see "m11 lesson, section Test doubles"
 */
public final class StubPriceLookup implements PriceLookup {

    private final Map<String, Long> cannedPrices;

    public StubPriceLookup(Map<String, Long> cannedPrices) {
        this.cannedPrices = Map.copyOf(cannedPrices);
    }

    @Override
    public long priceCents(String sku) {
        Long price = cannedPrices.get(sku);
        if (price == null) {
            throw new IllegalArgumentException("no canned price for " + sku);
        }
        return price;
    }
}
