package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps orders in memory.
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
public final class InMemoryOrderRepository implements OrderRepository {

    private final List<OrderRecord> orders = new ArrayList<>();

    @Override
    public synchronized void save(OrderRecord order) {
        orders.add(order);
    }

    @Override
    public synchronized List<OrderRecord> all() {
        return List.copyOf(orders);
    }
}
