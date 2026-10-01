package io.github.aliturgutbozkurt.patterns.m07.examples.observer;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic.PriceAlert;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic.PriceDisplay;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic.StockTicker;
import java.math.BigDecimal;

/**
 * Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/observer/StockTickerDemo.java}
 *
 * @see "m07 lesson, section Observer"
 */
public final class StockTickerDemo {

    private StockTickerDemo() {}

    public static void main(String[] args) {
        var ticker = new StockTicker("ACME", new BigDecimal("100.00"));
        var display = new PriceDisplay(System.out::println);
        ticker.attach(display);
        ticker.attach(new PriceAlert(new BigDecimal("105.00"), System.out::println));

        for (String price : new String[] {"101.50", "104.20", "105.10", "106.00", "106.00"}) {
            ticker.setPrice(new BigDecimal(price)); // the second 106.00 changes nothing: no notification
        }
        ticker.detach(display);
        System.out.println("-- display detached --");
        ticker.setPrice(new BigDecimal("99.80"));
    }
}
