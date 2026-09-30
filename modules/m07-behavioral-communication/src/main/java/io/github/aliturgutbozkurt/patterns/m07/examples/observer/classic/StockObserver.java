package io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic;

import java.math.BigDecimal;

/**
 * Observer: the only thing a {@link StockTicker} knows about the objects it notifies (push model — the subject
 * sends the symbol and the new price).
 *
 * @see "m07 lesson, section Observer"
 */
@FunctionalInterface
public interface StockObserver {

    /** Called after the price of {@code symbol} changed. */
    void update(String symbol, BigDecimal price);
}
