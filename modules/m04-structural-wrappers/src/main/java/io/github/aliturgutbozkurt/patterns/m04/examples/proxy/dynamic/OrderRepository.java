package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.dynamic;

import java.util.List;

/**
 * Demo interface for the dynamic proxies: orders by id.
 *
 * @see "m04 lesson, section Proxy"
 */
public interface OrderRepository {

    /** The order with this id; throws {@link java.util.NoSuchElementException} if there is none. */
    String findById(String id);

    List<String> findAllIds();

    @Mutator
    void save(String id, String order);

    /** A default method: its call to {@link #findById} goes through the proxy again. */
    default String describe(String id) {
        return id + ": " + findById(id);
    }
}
