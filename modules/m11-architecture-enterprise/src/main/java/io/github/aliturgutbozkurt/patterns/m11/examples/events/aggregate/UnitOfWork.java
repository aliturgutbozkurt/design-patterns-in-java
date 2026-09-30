package io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Unit of Work: collects the aggregates changed in one business operation, commits them together, and only
 * <em>then</em> dispatches their events. If the commit fails, no one hears about changes that never happened.
 *
 * @see "m11 lesson, section Domain events"
 */
public final class UnitOfWork {

    private final OrderStore store;
    private final DomainEventDispatcher<OrderEvent> dispatcher;
    private final List<Order> registered = new ArrayList<>();

    public UnitOfWork(OrderStore store, DomainEventDispatcher<OrderEvent> dispatcher) {
        this.store = Objects.requireNonNull(store, "store");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
    }

    /** Takes part in the next commit (registering twice is harmless). */
    public void register(Order order) {
        Objects.requireNonNull(order, "order");
        if (!registered.contains(order)) {
            registered.add(order);
        }
    }

    /** 1. save every registered order (may throw — then nothing is dispatched); 2. dispatch their events. */
    public void commit() {
        store.saveAll(registered);
        List<OrderEvent> events = new ArrayList<>();
        for (Order order : registered) {
            events.addAll(order.pullEvents());
        }
        registered.clear();
        dispatcher.dispatchAll(events);
    }

    /** Forgets the registered orders and their pending events. */
    public void rollback() {
        registered.forEach(Order::pullEvents);
        registered.clear();
    }
}
