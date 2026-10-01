package io.github.aliturgutbozkurt.patterns.m11.exercises.ex02;

import java.util.List;

/**
 * Assignment 02 — your aggregate: enforces the transitions and records one event per successful change. It never
 * calls a handler itself. (A suggested shape; you may change this class's API, it is not GIVEN.)
 */
public class Order {

    /** A new order with status {@code PLACED}; records {@code OrderPlaced}. */
    public static Order place(OrderId id, long totalCents) {
        // TODO(ex02): validate totalCents > 0, record the event.
        throw new UnsupportedOperationException("TODO(ex02): implement Order.place");
    }

    /** Rebuilds an order from storage; records nothing. */
    public static Order from(OrderSnapshot snapshot) {
        throw new UnsupportedOperationException("TODO(ex02): implement Order.from");
    }

    public void pay() {
        // TODO(ex02): only from PLACED, otherwise IllegalStateException and no event.
        throw new UnsupportedOperationException("TODO(ex02): implement pay()");
    }

    public void ship() {
        throw new UnsupportedOperationException("TODO(ex02): implement ship()");
    }

    public void cancel(String reason) {
        throw new UnsupportedOperationException("TODO(ex02): implement cancel(String)");
    }

    public OrderSnapshot snapshot() {
        throw new UnsupportedOperationException("TODO(ex02): implement snapshot()");
    }

    /** The recorded events; afterwards the aggregate's list is empty. */
    public List<OrderEvent> pullEvents() {
        throw new UnsupportedOperationException("TODO(ex02): implement pullEvents()");
    }
}
