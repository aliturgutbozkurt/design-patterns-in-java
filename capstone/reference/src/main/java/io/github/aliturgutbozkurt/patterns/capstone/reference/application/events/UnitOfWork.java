package io.github.aliturgutbozkurt.patterns.capstone.reference.application.events;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * The transaction boundary of the shop: every change of state runs inside {@link #run}, one at a time, and the events
 * it raised are dispatched only after it committed and released the lock — so a subscriber sees the stored state, may
 * itself change state, and a failed change notifies no one (adapted from modules/m11-…/events/aggregate/UnitOfWork.java).
 * A transaction started inside another one joins it; only the outermost dispatches.
 *
 * @see "capstone guide §1 Pattern map — Observer"
 */
@PatternRole(value = DesignPattern.DOMAIN_EVENTS, role = "unit of work (commit, then dispatch)")
public final class UnitOfWork {

    private final ReentrantLock lock = new ReentrantLock();
    private final ThreadLocal<Changes> current = new ThreadLocal<>();
    private final EventDispatcher dispatcher;

    public UnitOfWork(EventDispatcher dispatcher) {
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
    }

    /** Runs {@code work} as one transaction, then dispatches its events; returns the work's result. */
    public <T> T run(Function<Changes, T> work) {
        Committed<T> committed = runDeferred(work);
        dispatcher.dispatchAll(committed.events());
        return committed.result();
    }

    /** Runs {@code work} as one transaction and hands back its events instead of dispatching them. */
    public <T> Committed<T> runDeferred(Function<Changes, T> work) {
        lock.lock();
        Changes outer = current.get();
        Changes changes = outer != null ? outer : new Changes();
        if (outer == null) {
            current.set(changes);
        }
        try {
            T result = work.apply(changes);
            return new Committed<>(result, outer == null ? changes.events() : List.of());
        } finally {
            if (outer == null) {
                current.remove();
            }
            lock.unlock();
        }
    }

    /** Dispatches events committed earlier (e.g. by {@link #runDeferred} on worker threads). */
    public void dispatch(List<? extends ShopEvent> events) {
        dispatcher.dispatchAll(events);
    }
}
