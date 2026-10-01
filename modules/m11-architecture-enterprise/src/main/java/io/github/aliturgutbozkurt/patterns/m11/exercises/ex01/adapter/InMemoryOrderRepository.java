package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.adapter;

import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.Order;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.OrderRepository;
import java.util.List;
import java.util.Optional;

/** Assignment 01 — your outbound adapter: an in-memory Repository of orders. */
public class InMemoryOrderRepository implements OrderRepository {

    @Override
    public void save(Order order) {
        // TODO(ex01): store it; the same id again replaces the order but keeps its position.
        throw new UnsupportedOperationException("TODO(ex01): implement save(Order)");
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        throw new UnsupportedOperationException("TODO(ex01): implement findById(OrderId)");
    }

    @Override
    public List<Order> findByCustomer(String customer) {
        // TODO(ex01): first-save order; return an immutable list.
        throw new UnsupportedOperationException("TODO(ex01): implement findByCustomer(String)");
    }

    @Override
    public int count() {
        throw new UnsupportedOperationException("TODO(ex01): implement count()");
    }
}
