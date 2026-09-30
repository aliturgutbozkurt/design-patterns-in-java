package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderEvent;

/**
 * Outbound port for domain events; an in-process bus, an outbox or a broker can sit behind it.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
@FunctionalInterface
public interface EventPublisher {

    void publish(OrderEvent event);
}
