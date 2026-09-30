package io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Subsystem (in-memory fake): stock per sku. A reservation takes stock away all-or-nothing and can be released.
 *
 * @see "m05 lesson, section Facade — modern Java 27"
 */
public final class Inventory {

    private final Map<String, Integer> stock;
    private final Map<String, Map<String, Integer>> reservations = new HashMap<>();
    private int nextId = 1;

    public Inventory(Map<String, Integer> initialStock) {
        this.stock = new HashMap<>(initialStock);
    }

    /** Reserves every item or none; returns a reservation id, or empty when anything is short. */
    public synchronized Optional<String> reserve(Map<String, Integer> items) {
        for (var item : items.entrySet()) {
            if (available(item.getKey()) < item.getValue()) {
                return Optional.empty();
            }
        }
        items.forEach((sku, quantity) -> stock.merge(sku, -quantity, Integer::sum));
        String id = "R-" + nextId++;
        reservations.put(id, Map.copyOf(items));
        return Optional.of(id);
    }

    /** Puts the reserved items back; unknown or already released ids are ignored. */
    public synchronized void release(String reservationId) {
        Map<String, Integer> items = reservations.remove(reservationId);
        if (items != null) {
            items.forEach((sku, quantity) -> stock.merge(sku, quantity, Integer::sum));
        }
    }

    public synchronized int available(String sku) {
        return stock.getOrDefault(sku, 0);
    }
}
