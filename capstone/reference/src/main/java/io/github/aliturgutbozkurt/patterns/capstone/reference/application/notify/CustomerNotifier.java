package io.github.aliturgutbozkurt.patterns.capstone.reference.application.notify;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderCancelled;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderPaid;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderShipped;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvents;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.Notifier;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.OrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;
import java.util.Objects;

/**
 * Tells customers about their orders: confirmed (on payment), shipped (with the tracking code), cancelled (with the
 * reason). Reacts to committed events, so it can read the order it is told about.
 *
 * @see "capstone guide, Pattern map — Observer"
 */
@PatternRole(value = DesignPattern.OBSERVER, role = "concrete observer")
public final class CustomerNotifier {

    private final OrderRepository orders;
    private final Notifier notifier;

    public CustomerNotifier(OrderRepository orders, Notifier notifier) {
        this.orders = Objects.requireNonNull(orders, "orders");
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    /** Subscribes this observer to the events it reacts to. */
    public void subscribeTo(ShopEvents events) {
        events.subscribe(OrderPaid.class, this::confirmed);
        events.subscribe(OrderShipped.class, this::shipped);
        events.subscribe(OrderCancelled.class, this::cancelled);
    }

    private void confirmed(OrderPaid event) {
        Order order = order(event.order());
        send(order, "confirmed", "Thank you! We received " + order.total().toPlainString() + " for "
                + order.id().value() + ".");
    }

    private void shipped(OrderShipped event) {
        send(order(event.order()), "shipped", "Your order is on its way. Tracking code: " + event.trackingCode() + ".");
    }

    private void cancelled(OrderCancelled event) {
        send(order(event.order()), "cancelled", "Your order was cancelled: " + event.reason() + "."
                + (event.refunded() ? " The payment is refunded." : ""));
    }

    private Order order(OrderId id) {
        return orders.find(id).orElseThrow(() -> new IllegalStateException("event for unknown order " + id.value()));
    }

    private void send(Order order, String what, String body) {
        notifier.notify(order.customer().value(), "Order " + order.id().value() + " " + what, body);
    }
}
