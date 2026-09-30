package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.after;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * An ordinary object holding stock levels; create as many as you need.
 *
 * @see "m11 lesson, section Anti-patterns — Singleton and Service Locator abuse"
 */
public final class InMemoryStockLevels implements StockLevels {

    private final Map<String, Integer> levels = new TreeMap<>();

    public InMemoryStockLevels set(String sku, int units) {
        levels.put(Objects.requireNonNull(sku, "sku"), units);
        return this;
    }

    @Override
    public int levelOf(String sku) {
        return levels.getOrDefault(sku, 0);
    }

    @Override
    public List<String> skus() {
        return List.copyOf(levels.keySet());
    }
}
