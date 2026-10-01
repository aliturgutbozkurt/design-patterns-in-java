package io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.modern.Subscription;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Typed publish/subscribe: publishers and handlers only know the bus and the event types, never each other. Events
 * published from inside a handler are queued and delivered after the current event (no re-entrant recursion). If a
 * handler throws, the exception reaches the publisher and the events queued during that dispatch are dropped.
 * Single-threaded; not thread-safe.
 *
 * @see "m07 lesson, section Observer — typed event bus"
 */
public final class EventBus {

    /** A handler for events of type {@code E} (or a subtype). */
    private record Handler<E extends ShopEvent>(Class<E> type, Consumer<? super E> consumer) {

        /** Delivers {@code event} if it has the right type; returns whether it did. */
        boolean deliver(ShopEvent event) {
            if (!type.isInstance(event)) {
                return false;
            }
            consumer.accept(type.cast(event));
            return true;
        }
    }

    private final List<Handler<?>> handlers = new CopyOnWriteArrayList<>();
    private final Deque<ShopEvent> pending = new ArrayDeque<>();
    private final List<ShopEvent> deadEvents = new ArrayList<>();
    private boolean dispatching;

    /** Subscribes {@code handler} to events of {@code type} and its subtypes; close the handle to unsubscribe. */
    public <E extends ShopEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler) {
        // A private object per call: closing one subscription never removes another one with an equal handler.
        Handler<E> registration = new Handler<>(Objects.requireNonNull(type, "type"),
                Objects.requireNonNull(handler, "handler"));
        handlers.add(registration);
        return () -> handlers.removeIf(h -> h == registration);
    }

    /** Delivers {@code event} to every matching handler, in subscription order. */
    public void publish(ShopEvent event) {
        pending.addLast(Objects.requireNonNull(event, "event"));
        if (dispatching) {
            return; // called from inside a handler: the running loop below will deliver it next
        }
        dispatching = true;
        try {
            while (!pending.isEmpty()) {
                dispatch(pending.removeFirst());
            }
        } finally {
            dispatching = false;
            pending.clear(); // non-empty only if a handler threw: drop what it queued
        }
    }

    /** Events that no handler received, oldest first (an unmodifiable copy). */
    public List<ShopEvent> deadEvents() {
        return List.copyOf(deadEvents);
    }

    private void dispatch(ShopEvent event) {
        boolean delivered = false;
        for (Handler<?> handler : handlers) {
            delivered |= handler.deliver(event);
        }
        if (!delivered) {
            deadEvents.add(event);
        }
    }
}
