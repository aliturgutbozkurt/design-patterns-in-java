package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.resilience;

import java.util.Map;

/**
 * Fake warehouse: fails its first {@code failures} calls with a timeout, then answers from a fixed table.
 *
 * @see "m04 lesson, section Decorator"
 */
public final class FlakyStockService implements StockService {

    private final int failures;
    private final Map<String, Integer> stock;
    private int calls;

    public FlakyStockService(int failures, Map<String, Integer> stock) {
        if (failures < 0) {
            throw new IllegalArgumentException("failures must not be negative: " + failures);
        }
        this.failures = failures;
        this.stock = Map.copyOf(stock);
    }

    @Override
    public int available(String sku) {
        calls++;
        if (calls <= failures) {
            throw new IllegalStateException("warehouse timeout #" + calls);
        }
        return stock.getOrDefault(sku, 0);
    }

    /** How many times the warehouse was asked, including failed calls. */
    public int calls() {
        return calls;
    }
}
