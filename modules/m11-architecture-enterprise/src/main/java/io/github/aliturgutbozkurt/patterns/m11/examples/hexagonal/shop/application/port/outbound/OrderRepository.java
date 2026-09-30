package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import java.util.Optional;

/**
 * Outbound port: a Repository of orders, owned by the core and implemented by memory and file adapters.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public interface OrderRepository {

    /** Stores {@code order}, replacing an order with the same id. */
    void save(Order order);

    Optional<Order> findById(OrderId id);

    int count();
}
