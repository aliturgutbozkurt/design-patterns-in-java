package io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders;

import java.util.Optional;

/**
 * Repository of the {@link Order} aggregate: whole orders in, whole (copied) orders out — never single lines.
 *
 * @see "m11 lesson, section Repository — optimistic versioning"
 */
public interface OrderRepository {

    /** A fresh id: {@code order-1}, {@code order-2}, … */
    String nextId();

    /**
     * Stores {@code order} if it was loaded at the stored version, then increments the version.
     *
     * @throws ConcurrentUpdateException if someone else saved the order in between
     */
    void save(Order order);

    /** An isolated copy: changes to it are invisible until {@link #save}. */
    Optional<Order> findById(String id);
}
