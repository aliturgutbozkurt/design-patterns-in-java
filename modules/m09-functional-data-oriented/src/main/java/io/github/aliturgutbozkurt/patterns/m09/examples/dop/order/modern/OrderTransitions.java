package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern;

import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Cancelled;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Draft;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Paid;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Placed;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Shipped;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.TransitionError.AlreadyCancelled;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.TransitionError.AlreadyShipped;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.TransitionError.EmptyOrder;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result;

/**
 * State transitions as plain functions. The parameter type says which state a transition starts from, so shipping
 * an unpaid order does not compile; transitions that can still fail return a {@link Result}.
 *
 * @see "m09 lesson, section Data-oriented programming — Modern Java 27"
 */
public final class OrderTransitions {

    private OrderTransitions() {}

    public static Result<Placed, TransitionError> place(Draft draft) {
        return draft.lines().isEmpty()
                ? Result.err(new EmptyOrder(draft.id()))
                : Result.ok(new Placed(draft.id(), draft.lines()));
    }

    public static Paid pay(Placed placed, String paymentRef) {
        return new Paid(placed.id(), placed.lines(), paymentRef);
    }

    public static Shipped ship(Paid paid, String trackingCode) {
        return new Shipped(paid.id(), paid.lines(), paid.paymentRef(), trackingCode);
    }

    public static Result<Cancelled, TransitionError> cancel(Order order, String reason) {
        return switch (order) {
            case Draft(var id, var lines) -> Result.ok(new Cancelled(id, lines, reason, false));
            case Placed(var id, var lines) -> Result.ok(new Cancelled(id, lines, reason, false));
            case Paid(var id, var lines, _) -> Result.ok(new Cancelled(id, lines, reason, true));
            case Shipped s -> Result.err(new AlreadyShipped(s.id()));
            case Cancelled c -> Result.err(new AlreadyCancelled(c.id()));
        };
    }
}
