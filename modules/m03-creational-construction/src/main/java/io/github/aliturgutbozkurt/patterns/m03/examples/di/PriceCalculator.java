package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import java.math.BigDecimal;

/**
 * Prices items.
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
@FunctionalInterface
public interface PriceCalculator {

    /** @throws IllegalArgumentException for an unknown item */
    BigDecimal priceOf(String item, int quantity);
}
