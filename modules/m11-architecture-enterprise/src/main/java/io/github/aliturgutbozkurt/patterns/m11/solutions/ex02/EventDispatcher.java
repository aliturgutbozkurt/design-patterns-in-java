package io.github.aliturgutbozkurt.patterns.m11.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderEvent;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.Subscription;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Reference solution, assignment 02: typed, queue-based dispatch with an error handler.
 *
 * @see "m11 lesson, section Domain events"
 */
public final class EventDispatcher {

    private record Handler<E extends OrderEvent>(Class<E> type, Consumer<? super E> consumer) {

        void deliver(OrderEvent event) {
            if (type.isInstance(event)) {
                consumer.accept(type.cast(event));
            }
        }
    }

    private final List<Handler<?>> handlers = new CopyOnWriteArrayList<>();
    private final Deque<OrderEvent> queue = new ArrayDeque<>();
    private final Consumer<RuntimeException> errorHandler;
    private boolean dispatching;

    public EventDispatcher(Consumer<RuntimeException> errorHandler) {
        this.errorHandler = Objects.requireNonNull(errorHandler, "errorHandler");
    }

    public <E extends OrderEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler) {
        Handler<E> registration = new Handler<>(Objects.requireNonNull(type, "type"),
                Objects.requireNonNull(handler, "handler"));
        handlers.add(registration);
        return () -> handlers.removeIf(h -> h == registration); // identity: idempotent and exact
    }

    public void dispatch(List<OrderEvent> events) {
        queue.addAll(events);
        if (dispatching) {
            return;
        }
        dispatching = true;
        try {
            while (!queue.isEmpty()) {
                OrderEvent event = queue.removeFirst();
                for (Handler<?> handler : handlers) {
                    try {
                        handler.deliver(event);
                    } catch (RuntimeException e) {
                        errorHandler.accept(e);
                    }
                }
            }
        } finally {
            dispatching = false;
        }
    }
}
