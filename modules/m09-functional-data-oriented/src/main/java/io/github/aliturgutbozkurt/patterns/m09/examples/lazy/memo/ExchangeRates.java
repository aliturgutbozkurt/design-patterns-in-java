package io.github.aliturgutbozkurt.patterns.m09.examples.lazy.memo;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A stand-in for an expensive rate service: a fixed table plus a counter of how often it was asked.
 *
 * @see "m09 lesson, section Lazy evaluation and memoisation"
 */
public final class ExchangeRates {

    private static final Map<String, BigDecimal> TABLE = Map.of(
            "EUR/TRY", new BigDecimal("37.50"),
            "USD/TRY", new BigDecimal("34.20"),
            "EUR/USD", new BigDecimal("1.10"));

    private final AtomicInteger loads = new AtomicInteger();

    /** The "expensive" call. */
    public BigDecimal load(String pair) {
        loads.incrementAndGet();
        BigDecimal rate = TABLE.get(pair);
        if (rate == null) {
            throw new IllegalArgumentException("unknown currency pair: " + pair);
        }
        return rate;
    }

    public int loads() {
        return loads.get();
    }
}
