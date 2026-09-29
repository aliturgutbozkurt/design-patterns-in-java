package io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A handler for every {@link ShopEvent}: an exhaustive {@code switch} over the sealed type, so a new event type is a
 * compile error here instead of a silently ignored event.
 *
 * @see "m07 lesson, section Observer — typed event bus"
 */
public final class AuditLog implements Consumer<ShopEvent> {

    private final Consumer<String> out;

    public AuditLog(Consumer<String> out) {
        this.out = Objects.requireNonNull(out, "out");
    }

    @Override
    public void accept(ShopEvent event) {
        out.accept(switch (event) {
            case OrderPlaced(var id, var customer, var total) -> "order " + id + " placed by " + customer
                    + ", total " + total.toPlainString();
            case PaymentFailed(var id, var reason) -> "payment failed for " + id + ": " + reason;
            case OrderShipped(var id, var tracking) -> "order " + id + " shipped, tracking " + tracking;
        });
    }
}
