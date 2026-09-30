package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.resilience;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Decorator: writes one log line per call — its result or its failure — and changes nothing else.
 *
 * @see "m04 lesson, section Decorator"
 */
public record LoggingStockService(StockService target, Consumer<String> log) implements StockService {

    public LoggingStockService {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(log, "log");
    }

    @Override
    public int available(String sku) {
        try {
            int units = target.available(sku);
            log.accept("available(" + sku + ") = " + units);
            return units;
        } catch (RuntimeException e) {
            log.accept("available(" + sku + ") failed: " + e.getMessage());
            throw e;                                            // log, never swallow
        }
    }
}
