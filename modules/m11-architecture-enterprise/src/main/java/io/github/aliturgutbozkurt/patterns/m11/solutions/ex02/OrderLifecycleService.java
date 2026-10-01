package io.github.aliturgutbozkurt.patterns.m11.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderEvent;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderLifecycle;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderStatus;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderStore;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.Subscription;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Reference solution, assignment 02: every command is load → aggregate → commit → dispatch. A failed commit throws
 * before anything is dispatched.
 *
 * @see "m11 lesson, section Domain events"
 */
public final class OrderLifecycleService implements OrderLifecycle {

    private final OrderStore store;
    private final Supplier<OrderId> ids;
    private final EventDispatcher dispatcher;

    public OrderLifecycleService(OrderStore store, Supplier<OrderId> ids, Consumer<RuntimeException> errorHandler) {
        this.store = Objects.requireNonNull(store, "store");
        this.ids = Objects.requireNonNull(ids, "ids");
        this.dispatcher = new EventDispatcher(errorHandler);
    }

    @Override
    public OrderId place(long totalCents) {
        if (totalCents <= 0) {
            throw new IllegalArgumentException("total must be positive: " + totalCents);
        }
        Order order = Order.place(ids.get(), totalCents);
        commitThenDispatch(order);
        return order.snapshot().id();
    }

    @Override
    public void pay(OrderId id) {
        Order order = load(id);
        order.pay();
        commitThenDispatch(order);
    }

    @Override
    public void ship(OrderId id) {
        Order order = load(id);
        order.ship();
        commitThenDispatch(order);
    }

    @Override
    public void cancel(OrderId id, String reason) {
        Objects.requireNonNull(reason, "reason");
        Order order = load(id);
        order.cancel(reason);
        commitThenDispatch(order);
    }

    @Override
    public OrderStatus status(OrderId id) {
        return load(id).snapshot().status();
    }

    @Override
    public <E extends OrderEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler) {
        return dispatcher.subscribe(type, handler);
    }

    private Order load(OrderId id) {
        Objects.requireNonNull(id, "id");
        return store.load(id).map(Order::from).orElseThrow(() -> new NoSuchElementException("unknown order: " + id));
    }

    private void commitThenDispatch(Order order) {
        store.commit(order.snapshot());        // throws → the events below are never dispatched
        dispatcher.dispatch(order.pullEvents());
    }
}
