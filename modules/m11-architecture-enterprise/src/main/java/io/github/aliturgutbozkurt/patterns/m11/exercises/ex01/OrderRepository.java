package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.List;
import java.util.Optional;

/** GIVEN — do not modify. Outbound port: a Repository of confirmed orders. */
public interface OrderRepository {

    /** Stores {@code order}; saving an id again replaces the order but keeps its original position. */
    void save(Order order);

    Optional<Order> findById(OrderId id);

    /** The customer's orders in first-save order (an immutable list). */
    List<Order> findByCustomer(String customer);

    int count();
}
