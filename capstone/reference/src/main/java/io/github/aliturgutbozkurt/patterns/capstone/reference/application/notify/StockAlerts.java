package io.github.aliturgutbozkurt.patterns.capstone.reference.application.notify;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.StockLow;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvents;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.Notifier;
import java.util.Objects;

/**
 * Tells {@code ops} when a product's stock drops below the threshold.
 *
 * @see "capstone guide, Pattern map — Observer"
 */
@PatternRole(value = DesignPattern.OBSERVER, role = "concrete observer")
public final class StockAlerts {

    /** The recipient of stock alerts. */
    public static final String OPS = "ops";

    private final Notifier notifier;

    public StockAlerts(Notifier notifier) {
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    /** Subscribes this observer to {@link StockLow}. */
    public void subscribeTo(ShopEvents events) {
        events.subscribe(StockLow.class, this::alert);
    }

    private void alert(StockLow event) {
        notifier.notify(OPS, "Stock low: " + event.sku().value(),
                event.sku().value() + " has " + event.remaining() + " left.");
    }
}
