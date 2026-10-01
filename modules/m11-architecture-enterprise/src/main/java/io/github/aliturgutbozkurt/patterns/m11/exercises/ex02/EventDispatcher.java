package io.github.aliturgutbozkurt.patterns.m11.exercises.ex02;

import java.util.List;
import java.util.function.Consumer;

/**
 * Assignment 02 — your in-process dispatcher: typed subscriptions, subscription order, failing handlers go to the
 * error handler, and events dispatched while a dispatch is running wait in a queue. (A suggested shape.)
 */
public class EventDispatcher {

    private final Consumer<RuntimeException> errorHandler;

    public EventDispatcher(Consumer<RuntimeException> errorHandler) {
        // TODO(ex02): reject a null error handler.
        this.errorHandler = errorHandler;
    }

    public <E extends OrderEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler) {
        // TODO(ex02): register; the Subscription removes exactly this registration, closing twice is harmless.
        throw new UnsupportedOperationException("TODO(ex02): implement subscribe(Class, Consumer)");
    }

    public void dispatch(List<OrderEvent> events) {
        // TODO(ex02): queue the events; if a dispatch is already running, return — the running loop delivers them.
        throw new UnsupportedOperationException("TODO(ex02): implement dispatch(List)");
    }
}
