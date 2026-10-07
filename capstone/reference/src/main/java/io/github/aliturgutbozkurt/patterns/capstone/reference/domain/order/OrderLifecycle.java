package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState.Cancelled;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState.Delivered;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState.Paid;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState.Placed;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState.Shipped;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Transition.Allowed;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Transition.Refused;

/**
 * The allowed transitions of brief §2.2 (F7), one exhaustive {@code switch} per event: {@code PLACED → PAID},
 * {@code PAID → SHIPPED}, {@code SHIPPED → DELIVERED}, {@code PAID → CANCELLED}. Every other combination is refused.
 *
 * @see "capstone guide §1 Pattern map — State"
 */
@PatternRole(value = DesignPattern.STATE, role = "transitions per state (exhaustive switch)")
public final class OrderLifecycle {

    private OrderLifecycle() {
    }

    /** {@code PLACED → PAID}. */
    public static Transition pay(OrderState from, String paymentReference) {
        return switch (from) {
            case Placed _ -> new Allowed(new Paid(paymentReference), paymentReference);
            case Paid _, Shipped _, Delivered _, Cancelled _ -> refuse("pay", from);
        };
    }

    /** {@code PAID → SHIPPED}. */
    public static Transition ship(OrderState from, String trackingCode) {
        return switch (from) {
            case Paid(var reference) -> new Allowed(new Shipped(reference, trackingCode), trackingCode);
            case Placed _, Shipped _, Delivered _, Cancelled _ -> refuse("ship", from);
        };
    }

    /** {@code SHIPPED → DELIVERED}. */
    public static Transition deliver(OrderState from) {
        return switch (from) {
            case Shipped(var reference, var tracking) -> new Allowed(new Delivered(reference, tracking), "");
            case Placed _, Paid _, Delivered _, Cancelled _ -> refuse("deliver", from);
        };
    }

    /** {@code PAID → CANCELLED}; a blank reason is refused with {@code missing reason}. */
    public static Transition cancel(OrderState from, String reason) {
        return switch (from) {
            case Paid _ when reason.isBlank() -> new Refused("missing reason");
            case Paid(var reference) -> new Allowed(new Cancelled(reference, reason), reason);
            case Placed _, Shipped _, Delivered _, Cancelled _ -> refuse("cancel", from);
        };
    }

    private static Refused refuse(String action, OrderState from) {
        return new Refused("cannot " + action + " " + from.status() + " order");
    }
}
