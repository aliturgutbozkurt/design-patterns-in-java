package io.github.aliturgutbozkurt.patterns.m02.examples.singleton.holder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lazy holder idiom: the instance lives in a nested class that the JVM initializes only when {@link #getInstance()}
 * first touches it. Class initialization is thread-safe by the JLS, so no {@code synchronized} is needed.
 *
 * @see "m02 lesson, section Singleton"
 */
public final class CurrencyTable {

    // Mutable static state, allowed only in the Singleton lesson: counts constructions so tests can prove laziness.
    private static final AtomicInteger CREATIONS = new AtomicInteger();

    private final Map<String, BigDecimal> ratesToEur;

    private CurrencyTable() {
        CREATIONS.incrementAndGet();
        // Fixed demo rates; a real table would be loaded from a service — the expensive work worth delaying.
        ratesToEur = Map.of(
                "EUR", BigDecimal.ONE,
                "USD", new BigDecimal("0.92"),
                "TRY", new BigDecimal("0.027"));
    }

    private static final class Holder {
        static final CurrencyTable INSTANCE = new CurrencyTable();
    }

    public static CurrencyTable getInstance() {
        return Holder.INSTANCE;
    }

    /** How many instances have been created in this class loader (0 or 1). */
    public static int creations() {
        return CREATIONS.get();
    }

    /** Converts {@code amount} of {@code currency} to euros, scale 2, half-even. */
    public BigDecimal toEur(BigDecimal amount, String currency) {
        BigDecimal rate = ratesToEur.get(currency);
        if (rate == null) {
            throw new IllegalArgumentException("no rate for " + currency);
        }
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);
    }
}
