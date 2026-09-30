package io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Concrete observer that shows every new price on a screen (any {@code Consumer<String>}).
 *
 * @see "m07 lesson, section Observer"
 */
public final class PriceDisplay implements StockObserver {

    private final Consumer<String> screen;

    public PriceDisplay(Consumer<String> screen) {
        this.screen = Objects.requireNonNull(screen, "screen");
    }

    @Override
    public void update(String symbol, BigDecimal price) {
        screen.accept(symbol + " " + price.toPlainString());
    }
}
