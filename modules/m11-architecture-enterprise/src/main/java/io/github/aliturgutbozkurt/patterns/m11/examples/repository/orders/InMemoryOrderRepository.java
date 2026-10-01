package io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory stand-in for what JPA's {@code @Version} does: store copies, compare versions on save.
 *
 * @see "m11 lesson, section Repository — optimistic versioning"
 */
public final class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> stored = new HashMap<>();
    private long lastNumber;

    @Override
    public synchronized String nextId() {
        return "order-" + ++lastNumber;
    }

    @Override
    public synchronized void save(Order order) {
        Objects.requireNonNull(order, "order");
        Order current = stored.get(order.id());
        long storedVersion = current == null ? 0 : current.version();
        if (order.version() != storedVersion) {
            throw new ConcurrentUpdateException(order.id(), order.version(), storedVersion);
        }
        order.savedAt(storedVersion + 1);
        stored.put(order.id(), order.copy());
    }

    @Override
    public synchronized Optional<Order> findById(String id) {
        return Optional.ofNullable(stored.get(Objects.requireNonNull(id, "id"))).map(Order::copy);
    }
}
