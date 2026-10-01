package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.memory;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.OrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;

/**
 * Outbound adapter: orders in a map, in save order.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class InMemoryOrderRepository implements OrderRepository {

    private final SequencedMap<OrderId, Order> orders = new LinkedHashMap<>();

    @Override
    public void save(Order order) {
        orders.put(Objects.requireNonNull(order, "order").id(), order);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(orders.get(Objects.requireNonNull(id, "id")));
    }

    @Override
    public int count() {
        return orders.size();
    }
}
