package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.resilience;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Decorator: retries a failing call immediately, up to {@code maxAttempts} attempts in total.
 *
 * @see "m04 lesson, section Decorator"
 */
public record RetryingStockService(StockService target, int maxAttempts) implements StockService {

    public RetryingStockService {
        Objects.requireNonNull(target, "target");
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1: " + maxAttempts);
        }
    }

    @Override
    public int available(String sku) {
        List<RuntimeException> failures = new ArrayList<>();
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return target.available(sku);
            } catch (RuntimeException e) {
                failures.add(e);                                 // no sleeping: back-off is out of scope
            }
        }
        RuntimeException last = failures.removeLast();
        failures.forEach(last::addSuppressed);                  // keep the history, throw the latest
        throw last;
    }
}
