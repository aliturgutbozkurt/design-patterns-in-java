package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.dynamic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Real subject for the dynamic proxies: orders kept in a sorted map.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class InMemoryOrderRepository implements OrderRepository {

    private final SortedMap<String, String> orders = new TreeMap<>();

    @Override
    public String findById(String id) {
        String order = orders.get(id);
        if (order == null) {
            throw new NoSuchElementException("no order " + id);
        }
        return order;
    }

    @Override
    public List<String> findAllIds() {
        return List.copyOf(orders.keySet());
    }

    @Override
    public void save(String id, String order) {
        orders.put(id, order);
    }

    @Override
    public String toString() {
        return "InMemoryOrderRepository" + orders.keySet();
    }
}
