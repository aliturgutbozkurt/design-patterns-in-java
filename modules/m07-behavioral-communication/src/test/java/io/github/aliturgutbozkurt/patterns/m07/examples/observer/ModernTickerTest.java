package io.github.aliturgutbozkurt.patterns.m07.examples.observer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.modern.PriceChange;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.modern.Subscription;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.modern.Ticker;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class ModernTickerTest {

    private final List<RuntimeException> errors = new ArrayList<>();
    private final Ticker ticker = new Ticker("ACME", new BigDecimal("100.00"), errors::add);
    private final List<String> events = new ArrayList<>();

    @Test
    void listenerReceivesOldAndNewPrice() {
        List<PriceChange> changes = new ArrayList<>();
        ticker.onPriceChange(changes::add);
        ticker.setPrice(new BigDecimal("101.00"));
        assertThat(changes).containsExactly(
                new PriceChange("ACME", new BigDecimal("100.00"), new BigDecimal("101.00")));
    }

    @Test
    void closeStopsDeliveryAndIsIdempotent() {
        Subscription subscription = ticker.onPriceChange(change -> events.add("a " + change.newPrice()));
        ticker.onPriceChange(change -> events.add("b " + change.newPrice()));
        ticker.setPrice(new BigDecimal("101.00"));
        subscription.close();
        subscription.close();
        ticker.setPrice(new BigDecimal("102.00"));
        assertThat(events).containsExactly("a 101.00", "b 101.00", "b 102.00");
    }

    @Test
    void closingOneOfTwoIdenticalRegistrationsKeepsTheOther() {
        Consumer<PriceChange> listener = change -> events.add(change.newPrice().toPlainString());
        Subscription first = ticker.onPriceChange(listener);
        ticker.onPriceChange(listener);
        first.close();
        first.close();
        ticker.setPrice(new BigDecimal("101.00"));
        assertThat(events).containsExactly("101.00");
    }

    @Test
    void tryWithResourcesUnsubscribes() {
        try (var _ = ticker.onPriceChange(change -> events.add(change.newPrice().toPlainString()))) {
            ticker.setPrice(new BigDecimal("101.00"));
        }
        ticker.setPrice(new BigDecimal("102.00"));
        assertThat(events).containsExactly("101.00");
    }

    @Test
    void failingListenerGoesToTheErrorHandlerAndOthersStillReceiveTheEvent() {
        var boom = new IllegalStateException("display offline");
        ticker.onPriceChange(change -> events.add("first"));
        ticker.onPriceChange(change -> {
            throw boom;
        });
        ticker.onPriceChange(change -> events.add("third"));
        ticker.setPrice(new BigDecimal("101.00"));
        assertThat(events).containsExactly("first", "third");
        assertThat(errors).containsExactly(boom);
    }

    @Test
    void listenerAddedDuringDeliveryReceivesOnlyLaterEvents() {
        ticker.onPriceChange(change -> {
            if (events.isEmpty()) {
                ticker.onPriceChange(later -> events.add("late " + later.newPrice()));
            }
            events.add("early " + change.newPrice());
        });
        ticker.setPrice(new BigDecimal("101.00"));
        ticker.setPrice(new BigDecimal("102.00"));
        assertThat(events).containsExactly("early 101.00", "early 102.00", "late 102.00");
    }

    @Test
    void unchangedPriceNotifiesNobody() {
        ticker.onPriceChange(change -> events.add("changed"));
        ticker.setPrice(new BigDecimal("100"));
        assertThat(events).isEmpty();
    }

    @Test
    void priceChangeRejectsNullComponents() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PriceChange(" ", BigDecimal.ONE, BigDecimal.TEN));
        assertThatNullPointerException()
                .isThrownBy(() -> new PriceChange("ACME", null, BigDecimal.TEN));
    }

    @Test
    void demoPrintsChangesErrorsAndUnsubscribes() {
        assertThat(Console.capture(() -> ModernTickerDemo.main(new String[0]))).isEqualTo("""
                chart: ACME 100.00 -> 101.50 (+1.50)
                error handler: ticker feed rejected ACME 101.50
                log:   ACME 101.50
                chart: ACME 101.50 -> 99.00 (-2.50)
                error handler: ticker feed rejected ACME 99.00
                log:   ACME 99.00
                -- chart closed (try-with-resources) --
                error handler: ticker feed rejected ACME 98.25
                log:   ACME 98.25
                """);
    }
}
