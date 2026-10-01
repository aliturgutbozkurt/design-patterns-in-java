package io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * In-process dispatcher for domain events — m07's typed event bus with two additions for m11: a failing handler is
 * reported to an injected error handler (it cannot undo a commit that already happened), and events dispatched from
 * inside a handler are queued behind the ones already waiting. Single-threaded.
 *
 * @param <B> the base type of the events, e.g. a sealed {@code OrderEvent}
 * @see "m11 lesson, section Domain events"
 */
public final class DomainEventDispatcher<B> {

    /** A subscription: a type and what to do with events of that type (or a subtype). */
    private record Handler<E>(Class<E> type, Consumer<? super E> consumer) {

        void deliver(Object event) {
            if (type.isInstance(event)) {
                consumer.accept(type.cast(event));
            }
        }
    }

    private final List<Handler<?>> handlers = new CopyOnWriteArrayList<>();
    private final Deque<B> queue = new ArrayDeque<>();
    private final Consumer<? super RuntimeException> errorHandler;
    private boolean dispatching;

    public DomainEventDispatcher(Consumer<? super RuntimeException> errorHandler) {
        this.errorHandler = Objects.requireNonNull(errorHandler, "errorHandler");
    }

    /** Calls {@code handler} for every event of {@code type} or a subtype, in subscription order. */
    public <E extends B> void subscribe(Class<E> type, Consumer<? super E> handler) {
        handlers.add(new Handler<>(Objects.requireNonNull(type, "type"), Objects.requireNonNull(handler, "handler")));
    }

    public void dispatch(B event) {
        dispatchAll(List.of(event));
    }

    /** Queues {@code events} in order and, unless a dispatch is already running, delivers the whole queue. */
    public void dispatchAll(List<? extends B> events) {
        events.forEach(event -> queue.addLast(Objects.requireNonNull(event, "event")));
        if (dispatching) {
            return; // called from a handler: the loop below delivers these after the current event
        }
        dispatching = true;
        try {
            while (!queue.isEmpty()) {
                deliver(queue.removeFirst());
            }
        } finally {
            dispatching = false;
        }
    }

    private void deliver(B event) {
        for (Handler<?> handler : handlers) {
            try {
                handler.deliver(event);
            } catch (RuntimeException e) { // reported, not swallowed: the other handlers still run
                errorHandler.accept(e);
            }
        }
    }
}
