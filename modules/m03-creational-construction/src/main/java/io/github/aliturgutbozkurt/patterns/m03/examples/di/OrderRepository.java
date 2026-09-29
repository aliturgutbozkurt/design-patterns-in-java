package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import java.util.List;

/**
 * Stores completed orders.
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
public interface OrderRepository {

    void save(OrderRecord order);

    List<OrderRecord> all();
}
