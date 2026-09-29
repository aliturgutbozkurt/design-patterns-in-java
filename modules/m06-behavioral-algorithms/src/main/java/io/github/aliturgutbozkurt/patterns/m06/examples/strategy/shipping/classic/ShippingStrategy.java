package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic;

import java.math.BigDecimal;

/**
 * Classic Strategy: one interface, one class per shipping algorithm; the {@link ShippingCalculator} only knows this type.
 *
 * @see "m06 lesson, section Strategy"
 */
public interface ShippingStrategy {

    /** The shipping cost for {@code parcel}, with two decimal places. */
    BigDecimal cost(Parcel parcel);

    /** A short name for printing, e.g. {@code "flat"}. */
    String name();
}
