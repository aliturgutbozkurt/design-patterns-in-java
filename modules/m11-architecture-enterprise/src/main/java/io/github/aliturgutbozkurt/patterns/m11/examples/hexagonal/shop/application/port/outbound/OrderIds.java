package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;

/**
 * Outbound port for identities: injected, so tests and demos get {@code order-1}, {@code order-2}, … instead of
 * random UUIDs.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
@FunctionalInterface
public interface OrderIds {

    OrderId next();
}
