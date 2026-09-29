package io.github.aliturgutbozkurt.patterns.m07.examples.observer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic.PriceAlert;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic.PriceDisplay;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic.StockObserver;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.classic.StockTicker;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class StockTickerTest {

    private final StockTicker ticker = new StockTicker("ACME", new BigDecimal("100.00"));
    private final List<String> events = new ArrayList<>();

    @Test
    void observersAreNotifiedInAttachOrder() {
        ticker.attach((symbol, price) -> events.add("first " + symbol + " " + price));
        ticker.attach((symbol, price) -> events.add("second " + symbol + " " + price));
        ticker.setPrice(new BigDecimal("101.50"));
        assertThat(events).containsExactly("first ACME 101.50", "second ACME 101.50");
    }

    @Test
    void detachedObserverReceivesNothing() {
        StockObserver observer = (symbol, price) -> events.add(symbol + " " + price);
        ticker.attach(observer);
        ticker.detach(observer);
        ticker.setPrice(new BigDecimal("101.50"));
        assertThat(events).isEmpty();
    }

    @Test
    void unchangedPriceNotifiesNobody() {
        ticker.attach((symbol, price) -> events.add(symbol + " " + price));
        ticker.setPrice(new BigDecimal("100.0"));
        assertThat(events).isEmpty();
        assertThat(ticker.price()).isEqualByComparingTo("100.00");
    }

    @Test
    void displayShowsEveryNewPrice() {
        ticker.attach(new PriceDisplay(events::add));
        ticker.setPrice(new BigDecimal("101.50"));
        ticker.setPrice(new BigDecimal("99.75"));
        assertThat(events).containsExactly("ACME 101.50", "ACME 99.75");
    }

    @Test
    void alertFiresOnlyWhenThePriceCrossesTheThreshold() {
        ticker.attach(new PriceAlert(new BigDecimal("105.00"), events::add));
        ticker.setPrice(new BigDecimal("104.00"));
        ticker.setPrice(new BigDecimal("106.00"));
        ticker.setPrice(new BigDecimal("107.00"));
        ticker.setPrice(new BigDecimal("103.00"));
        ticker.setPrice(new BigDecimal("102.00"));
        ticker.setPrice(new BigDecimal("105.00"));
        assertThat(events).containsExactly(
                "ALERT ACME rose above 105.00: 106.00",
                "ALERT ACME fell below 105.00: 103.00",
                "ALERT ACME rose above 105.00: 105.00");
    }

    @Test
    void observerThatDetachesItselfDuringNotificationCausesNoConcurrentModification() {
        StockObserver oneShot = new StockObserver() {
            @Override
            public void update(String symbol, BigDecimal price) {
                events.add("one-shot " + price);
                ticker.detach(this);
            }
        };
        ticker.attach(oneShot);
        ticker.attach((symbol, price) -> events.add("steady " + price));
        assertThatCode(() -> ticker.setPrice(new BigDecimal("101.00"))).doesNotThrowAnyException();
        ticker.setPrice(new BigDecimal("102.00"));
        assertThat(events).containsExactly("one-shot 101.00", "steady 101.00", "steady 102.00");
    }

    @Test
    void demoPrintsPricesAndCrossingAlerts() {
        assertThat(Console.capture(() -> StockTickerDemo.main(new String[0]))).isEqualTo("""
                ACME 101.50
                ACME 104.20
                ACME 105.10
                ALERT ACME rose above 105.00: 105.10
                ACME 106.00
                -- display detached --
                ALERT ACME fell below 105.00: 99.80
                """);
    }
}
