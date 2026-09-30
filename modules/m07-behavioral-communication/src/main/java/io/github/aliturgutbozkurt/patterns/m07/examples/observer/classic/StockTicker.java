package io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Subject of the classic GoF Observer: keeps a list of {@link StockObserver}s and pushes every price change to them.
 * Not thread-safe.
 *
 * @see "m07 lesson, section Observer"
 */
public final class StockTicker {

    private final String symbol;
    private final List<StockObserver> observers = new ArrayList<>();
    private BigDecimal price;

    public StockTicker(String symbol, BigDecimal initialPrice) {
        this.symbol = Objects.requireNonNull(symbol, "symbol");
        this.price = Objects.requireNonNull(initialPrice, "initialPrice");
    }

    public void attach(StockObserver observer) {
        observers.add(Objects.requireNonNull(observer, "observer"));
    }

    public void detach(StockObserver observer) {
        observers.remove(observer);
    }

    public BigDecimal price() {
        return price;
    }

    /** Changes the price and notifies the observers — unless the price did not change. */
    public void setPrice(BigDecimal newPrice) {
        Objects.requireNonNull(newPrice, "newPrice");
        if (price.compareTo(newPrice) == 0) {
            return;
        }
        price = newPrice;
        notifyObservers();
    }

    private void notifyObservers() {
        // Iterate over a snapshot: an observer may attach or detach (itself) while being notified.
        for (StockObserver observer : List.copyOf(observers)) {
            observer.update(symbol, price);
        }
    }
}
