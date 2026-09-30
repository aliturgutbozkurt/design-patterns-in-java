package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Repository extracted from the god class: persistence and ids, nothing else.
 *
 * @see "m11 lesson, section Anti-patterns — god class"
 */
public final class OrderRepository {

    private final Map<String, PlacedOrder> orders = new LinkedHashMap<>();
    private int counter;

    public String nextId() {
        return "order-" + ++counter;
    }

    public void save(PlacedOrder order) {
        orders.put(Objects.requireNonNull(order, "order").id(), order);
    }

    public Optional<PlacedOrder> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    public int count() {
        return orders.size();
    }
}
