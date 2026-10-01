package io.github.aliturgutbozkurt.patterns.m11.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderEvent;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderSnapshot;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reference solution, assignment 02: the aggregate guards its transitions and records events; it never dispatches.
 *
 * @see "m11 lesson, section Domain events"
 */
public final class Order {

    private final OrderId id;
    private final long totalCents;
    private OrderStatus status;
    private final List<OrderEvent> pending = new ArrayList<>();

    private Order(OrderId id, OrderStatus status, long totalCents) {
        this.id = Objects.requireNonNull(id, "id");
        this.status = Objects.requireNonNull(status, "status");
        this.totalCents = totalCents;
    }

    public static Order place(OrderId id, long totalCents) {
        if (totalCents <= 0) {
            throw new IllegalArgumentException("total must be positive: " + totalCents);
        }
        Order order = new Order(id, OrderStatus.PLACED, totalCents);
        order.pending.add(new OrderEvent.OrderPlaced(id, totalCents));
        return order;
    }

    public static Order from(OrderSnapshot snapshot) {
        return new Order(snapshot.id(), snapshot.status(), snapshot.totalCents());
    }

    public void pay() {
        status = transition("pay", OrderStatus.PAID, OrderStatus.PLACED);
        pending.add(new OrderEvent.OrderPaid(id));
    }

    public void ship() {
        status = transition("ship", OrderStatus.SHIPPED, OrderStatus.PAID);
        pending.add(new OrderEvent.OrderShipped(id));
    }

    public void cancel(String reason) {
        Objects.requireNonNull(reason, "reason");
        status = transition("cancel", OrderStatus.CANCELLED, OrderStatus.PLACED, OrderStatus.PAID);
        pending.add(new OrderEvent.OrderCancelled(id, reason));
    }

    public OrderSnapshot snapshot() {
        return new OrderSnapshot(id, status, totalCents);
    }

    public List<OrderEvent> pullEvents() {
        List<OrderEvent> events = List.copyOf(pending);
        pending.clear();
        return events;
    }

    private OrderStatus transition(String action, OrderStatus target, OrderStatus... allowedFrom) {
        if (!List.of(allowedFrom).contains(status)) {
            throw new IllegalStateException("cannot " + action + " " + id + ": it is " + status);
        }
        return target;
    }
}
