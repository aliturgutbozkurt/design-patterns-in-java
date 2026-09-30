package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.dynamic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Real subject: prices kept in a map.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class InMemoryPriceList implements PriceList {

    private final Map<String, BigDecimal> prices = new HashMap<>();

    public InMemoryPriceList(Map<String, BigDecimal> initialPrices) {
        prices.putAll(initialPrices);
    }

    @Override
    public BigDecimal priceOf(String sku) {
        BigDecimal price = prices.get(sku);
        if (price == null) {
            throw new NoSuchElementException("no price for " + sku);
        }
        return price;
    }

    @Override
    public void changePrice(String sku, BigDecimal price) {
        prices.put(sku, price);
    }
}
