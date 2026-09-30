package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Order aggregate of the PatternShop core. It records the events it raises; the application service hands them to
 * the {@code EventPublisher} port only after the order is saved. Pure Java: no adapter, no framework.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class Order {

    private final OrderId id;
    private final String customer;
    private final List<OrderLine> lines;
    private final Money total;
    private final List<OrderEvent> pendingEvents = new ArrayList<>();

    private Order(OrderId id, String customer, List<OrderLine> lines) {
        this.id = Objects.requireNonNull(id, "id");
        this.customer = Objects.requireNonNull(customer, "customer");
        this.lines = List.copyOf(lines);
        if (customer.isBlank()) {
            throw new IllegalArgumentException("blank customer");
        }
        if (this.lines.isEmpty()) {
            throw new IllegalArgumentException("an order needs at least one line");
        }
        this.total = totalOf(this.lines);
    }

    /** Places a new order and records {@link OrderEvent.OrderPlaced}. */
    public static Order place(OrderId id, String customer, List<OrderLine> lines) {
        Order order = new Order(id, customer, lines);
        order.pendingEvents.add(new OrderEvent.OrderPlaced(order.id, order.customer, order.total));
        return order;
    }

    /** Rebuilds a stored order (a repository loading it): nothing happened, so no event is recorded. */
    public static Order restore(OrderId id, String customer, List<OrderLine> lines) {
        return new Order(id, customer, lines);
    }

    /** Σ quantity × unit price. */
    public static Money totalOf(List<OrderLine> lines) {
        return lines.stream().map(OrderLine::total).reduce(Money.ZERO, Money::plus);
    }

    public OrderId id() {
        return id;
    }

    public String customer() {
        return customer;
    }

    public List<OrderLine> lines() {
        return lines;
    }

    public Money total() {
        return total;
    }

    /** Hands out the recorded events and forgets them, so no event is published twice. */
    public List<OrderEvent> pullEvents() {
        List<OrderEvent> events = List.copyOf(pendingEvents);
        pendingEvents.clear();
        return events;
    }
}
