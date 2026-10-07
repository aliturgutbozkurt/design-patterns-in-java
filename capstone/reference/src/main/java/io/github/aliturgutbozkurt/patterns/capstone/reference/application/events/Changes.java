package io.github.aliturgutbozkurt.patterns.capstone.reference.application.events;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * What one {@link UnitOfWork} transaction did: the events it raised, in order, and how to undo the writes it made.
 *
 * @see "capstone guide, Pattern map — Observer"
 */
public final class Changes {

    private final List<ShopEvent> events = new ArrayList<>();
    private final Deque<Runnable> undos = new ArrayDeque<>();

    /** Records an event; it is dispatched only if the transaction commits. */
    public void raise(ShopEvent event) {
        events.add(Objects.requireNonNull(event, "event"));
    }

    /** The events so far (a copy). */
    public List<ShopEvent> events() {
        return List.copyOf(events);
    }

    /** Records how to undo a write that was just made; it runs only if the transaction fails. */
    public void onRollback(Runnable undo) {
        undos.push(Objects.requireNonNull(undo, "undo"));
    }

    /** Undoes the recorded writes, newest first; an undo that fails is attached to {@code failure}. */
    void rollBack(RuntimeException failure) {
        while (!undos.isEmpty()) {
            try {
                undos.pop().run();
            } catch (RuntimeException e) {
                failure.addSuppressed(e); // reported with the original failure, not swallowed
            }
        }
    }
}
