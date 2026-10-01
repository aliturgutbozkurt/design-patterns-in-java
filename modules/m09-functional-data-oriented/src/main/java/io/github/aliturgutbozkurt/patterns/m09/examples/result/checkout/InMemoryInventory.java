package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fake stock store that logs every reservation and release, so tests can see which steps ran.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public final class InMemoryInventory {

    private final Map<String, Integer> stock;
    private final List<String> log = new ArrayList<>();

    public InMemoryInventory(Map<String, Integer> stock) {
        this.stock = new HashMap<>(stock);
    }

    public int available(String sku) {
        return stock.getOrDefault(sku, 0);
    }

    public void reserve(CartItem item) {
        int left = available(item.sku()) - item.quantity();
        if (left < 0) {
            throw new IllegalStateException("not enough " + item.sku() + " to reserve " + item.quantity());
        }
        stock.put(item.sku(), left);
        log.add("reserve " + item.sku() + " x" + item.quantity());
    }

    public void release(CartItem item) {
        stock.merge(item.sku(), item.quantity(), Integer::sum);
        log.add("release " + item.sku() + " x" + item.quantity());
    }

    public List<String> log() {
        return List.copyOf(log);
    }
}
