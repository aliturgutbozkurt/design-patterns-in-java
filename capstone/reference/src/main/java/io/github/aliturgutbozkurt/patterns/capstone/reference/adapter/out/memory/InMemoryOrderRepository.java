package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.OrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Orders in a concurrent map sorted by order number ({@code order-2} before {@code order-10}).
 *
 * @see "capstone guide, Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.REPOSITORY, role = "in-memory repository (outbound adapter)")
public final class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> orders = new ConcurrentSkipListMap<>();

    @Override
    public void save(Order order) {
        orders.put(Objects.requireNonNull(order, "order").id(), order);
    }

    @Override
    public void remove(OrderId id) {
        orders.remove(Objects.requireNonNull(id, "id"));
    }

    @Override
    public Optional<Order> find(OrderId id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(orders.values());
    }
}
