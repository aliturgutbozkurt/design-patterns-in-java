package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderCancelled;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderDelivered;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderView;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.TransitionResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.TransitionResult.Done;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.TransitionResult.Refused;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.Changes;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.UnitOfWork;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.OrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentOutcome;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentPort;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderLifecycle;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Transition;
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Feature F7: reading orders and the customer-initiated transitions, each committed with its event.
 *
 * @see "capstone guide §2 Slice walkthrough — C5"
 */
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "application service behind an inbound port")
public final class OrderService implements OrderUseCase {

    private final OrderRepository orders;
    private final PaymentPort payments;
    private final Inventory inventory;
    private final Clock clock;
    private final UnitOfWork unitOfWork;

    public OrderService(OrderRepository orders, PaymentPort payments, Inventory inventory, Clock clock,
                        UnitOfWork unitOfWork) {
        this.orders = Objects.requireNonNull(orders, "orders");
        this.payments = Objects.requireNonNull(payments, "payments");
        this.inventory = Objects.requireNonNull(inventory, "inventory");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.unitOfWork = Objects.requireNonNull(unitOfWork, "unitOfWork");
    }

    @Override
    public Optional<OrderView> find(OrderId order) {
        return orders.find(order).map(Views::of);
    }

    @Override
    public List<OrderView> ordersOf(CustomerId customer) {
        return orders.findAll().stream().filter(order -> order.customer().equals(customer)).map(Views::of).toList();
    }

    @Override
    public TransitionResult cancel(OrderId id, String reason) {
        Objects.requireNonNull(reason, "reason");
        return unitOfWork.run(changes -> {
            Optional<Order> found = orders.find(id);
            if (found.isEmpty()) {
                return unknown(id);
            }
            Order order = found.get();
            return switch (OrderLifecycle.cancel(order.state(), reason)) {
                case Transition.Refused(var why) -> new Refused(why);
                case Transition.Allowed allowed -> refundAndCancel(order, allowed, reason, changes);
            };
        });
    }

    @Override
    public TransitionResult markDelivered(OrderId id) {
        return unitOfWork.run(changes -> {
            Optional<Order> found = orders.find(id);
            if (found.isEmpty()) {
                return unknown(id);
            }
            Order order = found.get();
            return switch (OrderLifecycle.deliver(order.state())) {
                case Transition.Refused(var why) -> new Refused(why);
                case Transition.Allowed allowed -> commit(order, allowed, new OrderDelivered(id), changes);
            };
        });
    }

    private TransitionResult refundAndCancel(Order order, Transition.Allowed allowed, String reason,
                                             Changes changes) {
        boolean refunded = allowed.next() instanceof OrderState.Cancelled cancelled && cancelled.refunded();
        if (refunded && !(payments.refund(order.state().paymentReference(), order.total())
                instanceof PaymentOutcome.Approved)) {
            return new Refused("refund failed");
        }
        inventory.release(order.items());
        return commit(order, allowed, new OrderCancelled(order.id(), reason, refunded), changes);
    }

    private Done commit(Order order, Transition.Allowed allowed, ShopEvent event, Changes changes) {
        Order changed = order.after(allowed, clock.instant());
        orders.save(changed);
        changes.raise(event);
        return new Done(Views.of(changed));
    }

    private static Refused unknown(OrderId id) {
        return new Refused("unknown order: " + id.value());
    }
}
