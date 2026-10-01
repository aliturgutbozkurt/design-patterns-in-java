package io.github.aliturgutbozkurt.patterns.m11.solutions.ex01.adapter;

import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.Order;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.OrderRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;

/**
 * Reference solution, assignment 01: an insertion-ordered map — {@code put} on an existing key keeps its position.
 *
 * @see "m11 lesson, section Repository"
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
    public List<Order> findByCustomer(String customer) {
        Objects.requireNonNull(customer, "customer");
        return orders.values().stream().filter(order -> order.customer().equals(customer)).toList();
    }

    @Override
    public int count() {
        return orders.size();
    }
}
