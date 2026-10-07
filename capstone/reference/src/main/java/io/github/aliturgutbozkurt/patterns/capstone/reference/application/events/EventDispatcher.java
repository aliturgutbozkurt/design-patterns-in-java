package io.github.aliturgutbozkurt.patterns.capstone.reference.application.events;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvents;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.Subscription;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Typed publish/subscribe for the shop's domain events (adapted from modules/m07-…/observer/eventbus/EventBus.java).
 * A handler receives its event type and subtypes, in subscription order. Events dispatched from inside a handler are
 * queued and delivered after the current one (per thread, so concurrent callers never mix their queues). A handler
 * that throws is reported to the error sink; the other handlers still run.
 *
 * @see "capstone guide §1 Pattern map — Observer"
 */
@PatternRole(value = DesignPattern.OBSERVER, role = "subject (typed event bus)")
@PatternRole(value = DesignPattern.DOMAIN_EVENTS, role = "dispatcher")
public final class EventDispatcher implements ShopEvents {

    /** One subscription: a private object per call, so closing it never removes another subscription. */
    private record Handler<E extends ShopEvent>(Class<E> type, Consumer<? super E> consumer) {

        void deliver(ShopEvent event) {
            if (type.isInstance(event)) {
                consumer.accept(type.cast(event));
            }
        }
    }

    private final List<Handler<?>> handlers = new CopyOnWriteArrayList<>();
    private final ThreadLocal<Deque<ShopEvent>> dispatching = new ThreadLocal<>();
    private final Consumer<Throwable> errors;

    public EventDispatcher(Consumer<Throwable> errors) {
        this.errors = Objects.requireNonNull(errors, "errors");
    }

    @Override
    public <E extends ShopEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler) {
        Handler<E> registration = new Handler<>(Objects.requireNonNull(type, "type"),
                Objects.requireNonNull(handler, "handler"));
        handlers.add(registration);
        return () -> handlers.removeIf(h -> h == registration);
    }

    /** Delivers committed events in order; called by the {@link UnitOfWork} after the change is stored. */
    public void dispatchAll(List<? extends ShopEvent> events) {
        Deque<ShopEvent> queue = dispatching.get();
        if (queue != null) {
            queue.addAll(events); // called from inside a handler: the running loop delivers them next
            return;
        }
        queue = new ArrayDeque<>(events);
        dispatching.set(queue);
        try {
            while (!queue.isEmpty()) {
                deliver(queue.removeFirst());
            }
        } finally {
            dispatching.remove();
        }
    }

    private void deliver(ShopEvent event) {
        for (Handler<?> handler : handlers) {
            try {
                handler.deliver(event);
            } catch (RuntimeException e) {
                errors.accept(e); // reported, not swallowed: the error sink is the shop's error channel
            }
        }
    }
}
