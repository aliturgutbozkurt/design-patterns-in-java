package io.github.aliturgutbozkurt.patterns.m11.exercises.ex02;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** Assignment 02 — your use case: load → aggregate method → commit → dispatch, for every command. */
public class OrderLifecycleService implements OrderLifecycle {

    private final OrderStore store;
    private final Supplier<OrderId> ids;
    private final Consumer<RuntimeException> errorHandler;

    public OrderLifecycleService(OrderStore store, Supplier<OrderId> ids, Consumer<RuntimeException> errorHandler) {
        // TODO(ex02): reject nulls and create your EventDispatcher with the error handler.
        this.store = store;
        this.ids = ids;
        this.errorHandler = errorHandler;
    }

    @Override
    public OrderId place(long totalCents) {
        throw new UnsupportedOperationException("TODO(ex02): implement place(long)");
    }

    @Override
    public void pay(OrderId id) {
        // TODO(ex02): unknown id → NoSuchElementException; commit first, dispatch after.
        throw new UnsupportedOperationException("TODO(ex02): implement pay(OrderId)");
    }

    @Override
    public void ship(OrderId id) {
        throw new UnsupportedOperationException("TODO(ex02): implement ship(OrderId)");
    }

    @Override
    public void cancel(OrderId id, String reason) {
        throw new UnsupportedOperationException("TODO(ex02): implement cancel(OrderId, String)");
    }

    @Override
    public OrderStatus status(OrderId id) {
        throw new UnsupportedOperationException("TODO(ex02): implement status(OrderId)");
    }

    @Override
    public <E extends OrderEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler) {
        throw new UnsupportedOperationException("TODO(ex02): implement subscribe(Class, Consumer)");
    }
}
