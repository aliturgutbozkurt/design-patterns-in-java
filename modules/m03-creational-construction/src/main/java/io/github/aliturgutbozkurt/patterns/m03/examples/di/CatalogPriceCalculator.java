package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Prices from a fixed catalog.
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
public final class CatalogPriceCalculator implements PriceCalculator {

    private final Map<String, BigDecimal> catalog;

    public CatalogPriceCalculator(Map<String, BigDecimal> catalog) {
        this.catalog = Map.copyOf(catalog);
    }

    @Override
    public BigDecimal priceOf(String item, int quantity) {
        BigDecimal unitPrice = catalog.get(item);
        if (unitPrice == null) {
            throw new IllegalArgumentException("unknown item: " + item);
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
