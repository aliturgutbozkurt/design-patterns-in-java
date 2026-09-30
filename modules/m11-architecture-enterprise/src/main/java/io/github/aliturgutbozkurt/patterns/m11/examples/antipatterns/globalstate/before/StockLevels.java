package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ANTI-PATTERN — see lesson: a Singleton holding mutable stock levels. There is exactly one stock table per JVM, so
 * two tests (or two tenants) share it whether they want to or not.
 *
 * @see "m11 lesson, section Anti-patterns — Singleton and Service Locator abuse"
 */
public final class StockLevels {

    private static StockLevels instance; // ANTI-PATTERN: mutable static state

    private final Map<String, Integer> levels = new TreeMap<>();

    private StockLevels() {}

    public static StockLevels getInstance() {
        if (instance == null) {
            instance = new StockLevels();
        }
        return instance;
    }

    static void resetInstance() {
        instance = null;
    }

    public void set(String sku, int units) {
        levels.put(sku, units);
    }

    public int levelOf(String sku) {
        return levels.getOrDefault(sku, 0);
    }

    /** Every known SKU, sorted. */
    public List<String> skus() {
        return List.copyOf(levels.keySet());
    }
}
