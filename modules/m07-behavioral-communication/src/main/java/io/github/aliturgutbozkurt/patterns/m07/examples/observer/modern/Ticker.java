package io.github.aliturgutbozkurt.patterns.m07.examples.observer.modern;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Modern Observer subject: listeners are plain {@code Consumer}s, every subscription returns a {@link Subscription}
 * handle, and a failing listener is reported to the error handler instead of silencing the others.
 *
 * @see "m07 lesson, section Observer — Modern Java 27"
 */
public final class Ticker {

    /** One registration per subscribe call, compared by identity, so the same lambda may be subscribed twice. */
    private static final class Registration {
        private final Consumer<? super PriceChange> listener;

        private Registration(Consumer<? super PriceChange> listener) {
            this.listener = listener;
        }
    }

    private final String symbol;
    private final Consumer<RuntimeException> errorHandler;
    // Copy-on-write: delivery iterates over a snapshot, so (un)subscribing during delivery affects only later events.
    private final List<Registration> registrations = new CopyOnWriteArrayList<>();
    private BigDecimal price;

    public Ticker(String symbol, BigDecimal initialPrice, Consumer<RuntimeException> errorHandler) {
        this.symbol = Objects.requireNonNull(symbol, "symbol");
        this.price = Objects.requireNonNull(initialPrice, "initialPrice");
        this.errorHandler = Objects.requireNonNull(errorHandler, "errorHandler");
    }

    /** Subscribes {@code listener}; close the returned handle to unsubscribe. */
    public Subscription onPriceChange(Consumer<? super PriceChange> listener) {
        var registration = new Registration(Objects.requireNonNull(listener, "listener"));
        registrations.add(registration);
        return () -> registrations.remove(registration); // removing twice is harmless: idempotent
    }

    /** Changes the price and notifies every listener — unless the price did not change. */
    public void setPrice(BigDecimal newPrice) {
        Objects.requireNonNull(newPrice, "newPrice");
        if (price.compareTo(newPrice) == 0) {
            return;
        }
        var change = new PriceChange(symbol, price, newPrice);
        price = newPrice;
        for (Registration registration : registrations) {
            try {
                registration.listener.accept(change);
            } catch (RuntimeException e) {
                errorHandler.accept(e); // not swallowed: reported, and the remaining listeners still run
            }
        }
    }
}
