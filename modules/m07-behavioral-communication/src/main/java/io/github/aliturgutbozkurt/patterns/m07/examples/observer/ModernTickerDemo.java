package io.github.aliturgutbozkurt.patterns.m07.examples.observer;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.modern.PriceChange;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.modern.Ticker;
import java.math.BigDecimal;

/** Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/observer/ModernTickerDemo.java} */
public final class ModernTickerDemo {

    private ModernTickerDemo() {}

    public static void main(String[] args) {
        var ticker = new Ticker("ACME", new BigDecimal("100.00"),
                error -> System.out.println("error handler: " + error.getMessage()));

        try (var _ = ticker.onPriceChange(ModernTickerDemo::chart)) {
            ticker.onPriceChange(change -> {
                throw new IllegalStateException("ticker feed rejected " + change.symbol() + " " + change.newPrice());
            });
            ticker.onPriceChange(change -> System.out.println("log:   " + change.symbol() + " " + change.newPrice()));
            ticker.setPrice(new BigDecimal("101.50"));
            ticker.setPrice(new BigDecimal("99.00"));
        }
        System.out.println("-- chart closed (try-with-resources) --");
        ticker.setPrice(new BigDecimal("98.25"));
    }

    private static void chart(PriceChange change) {
        String sign = change.delta().signum() >= 0 ? "+" : "";
        System.out.println("chart: " + change.symbol() + " " + change.oldPrice() + " -> " + change.newPrice()
                + " (" + sign + change.delta() + ")");
    }
}
