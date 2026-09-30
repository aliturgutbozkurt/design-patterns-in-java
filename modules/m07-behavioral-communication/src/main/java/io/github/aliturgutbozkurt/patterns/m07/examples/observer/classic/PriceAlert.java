package io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Concrete observer with its own state: raises an alert only when the price <em>crosses</em> the threshold, not on
 * every update above it. The price is assumed to start below the threshold.
 *
 * @see "m07 lesson, section Observer"
 */
public final class PriceAlert implements StockObserver {

    private final BigDecimal threshold;
    private final Consumer<String> alerts;
    private boolean above;

    public PriceAlert(BigDecimal threshold, Consumer<String> alerts) {
        this.threshold = Objects.requireNonNull(threshold, "threshold");
        this.alerts = Objects.requireNonNull(alerts, "alerts");
    }

    @Override
    public void update(String symbol, BigDecimal price) {
        boolean nowAbove = price.compareTo(threshold) >= 0;
        if (nowAbove != above) {
            String direction = nowAbove ? " rose above " : " fell below ";
            alerts.accept("ALERT " + symbol + direction + threshold.toPlainString() + ": " + price.toPlainString());
        }
        above = nowAbove;
    }
}
