package io.github.aliturgutbozkurt.patterns.capstone.reference.application.events;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import java.util.List;

/**
 * The result of a committed transaction and the events it raised, not yet dispatched.
 *
 * @param result the transaction's result
 * @param events the raised events in order
 * @param <T>    the result type
 * @see "capstone guide, Pattern map — Observer"
 */
public record Committed<T>(T result, List<ShopEvent> events) {

    public Committed {
        events = List.copyOf(events);
    }
}
