package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.modern;

import io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic.Parcel;
import java.math.BigDecimal;

/**
 * Modern Strategy: a functional interface, so every shipping algorithm can be a lambda or a method reference.
 *
 * @see "m06 lesson, section Strategy"
 */
@FunctionalInterface
public interface ShippingRule {

    /** The shipping cost for {@code parcel}. */
    BigDecimal cost(Parcel parcel);
}
