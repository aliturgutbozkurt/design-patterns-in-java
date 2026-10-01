package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;

/**
 * Outbound port in the core's own words; a payment provider's API is adapted to it, never the other way round.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
@FunctionalInterface
public interface PaymentPort {

    /** Charges {@code amount}; {@code true} if approved, {@code false} if declined. */
    boolean charge(String customer, Money amount);
}
