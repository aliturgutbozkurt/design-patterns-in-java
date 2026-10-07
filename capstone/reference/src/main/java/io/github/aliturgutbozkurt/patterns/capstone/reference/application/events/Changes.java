package io.github.aliturgutbozkurt.patterns.capstone.reference.application.events;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The events raised inside one {@link UnitOfWork} transaction, in the order they were raised.
 *
 * @see "capstone guide §1 Pattern map — Observer"
 */
public final class Changes {

    private final List<ShopEvent> events = new ArrayList<>();

    /** Records an event; it is dispatched only if the transaction commits. */
    public void raise(ShopEvent event) {
        events.add(Objects.requireNonNull(event, "event"));
    }

    /** The events so far (a copy). */
    public List<ShopEvent> events() {
        return List.copyOf(events);
    }
}
