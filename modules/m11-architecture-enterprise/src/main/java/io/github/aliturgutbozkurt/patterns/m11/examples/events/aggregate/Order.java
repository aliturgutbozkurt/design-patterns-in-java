package io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Order aggregate that <em>records</em> domain events instead of calling listeners. It decides what happened; the
 * {@link UnitOfWork} decides when the world hears about it.
 *
 * @see "m11 lesson, section Domain events"
 */
public final class Order {

    private final String id;
    private final long totalCents;
    private OrderStatus status;
    private final List<OrderEvent> pendingEvents = new ArrayList<>();

    private Order(String id, OrderStatus status, long totalCents) {
        this.id = Objects.requireNonNull(id, "id");
        this.status = Objects.requireNonNull(status, "status");
        if (totalCents <= 0) {
            throw new IllegalArgumentException("total must be positive: " + totalCents);
        }
        this.totalCents = totalCents;
    }

    /** A new order; records {@link OrderEvent.OrderPlaced}. */
    public static Order place(String id, long totalCents) {
        Order order = new Order(id, OrderStatus.PLACED, totalCents);
        order.pendingEvents.add(new OrderEvent.OrderPlaced(id, totalCents));
        return order;
    }

    /** An order loaded from storage: nothing happened, so nothing is recorded. */
    public static Order restore(String id, OrderStatus status, long totalCents) {
        return new Order(id, status, totalCents);
    }

    public void pay() {
        require("pay", OrderStatus.PLACED);
        status = OrderStatus.PAID;
        pendingEvents.add(new OrderEvent.OrderPaid(id));
    }

    public void ship() {
        require("ship", OrderStatus.PAID);
        status = OrderStatus.SHIPPED;
        pendingEvents.add(new OrderEvent.OrderShipped(id));
    }

    public void cancel(String reason) {
        Objects.requireNonNull(reason, "reason");
        require("cancel", OrderStatus.PLACED, OrderStatus.PAID);
        status = OrderStatus.CANCELLED;
        pendingEvents.add(new OrderEvent.OrderCancelled(id, reason));
    }

    /** Hands out the recorded events and forgets them: each event leaves the aggregate exactly once. */
    public List<OrderEvent> pullEvents() {
        List<OrderEvent> events = List.copyOf(pendingEvents);
        pendingEvents.clear();
        return events;
    }

    public String id() {
        return id;
    }

    public OrderStatus status() {
        return status;
    }

    public long totalCents() {
        return totalCents;
    }

    /** Guards a transition: throws, and records nothing, unless the order is in one of {@code allowed}. */
    private void require(String action, OrderStatus... allowed) {
        if (!List.of(allowed).contains(status)) {
            throw new IllegalStateException("cannot " + action + " " + id + ": it is " + status);
        }
    }
}
