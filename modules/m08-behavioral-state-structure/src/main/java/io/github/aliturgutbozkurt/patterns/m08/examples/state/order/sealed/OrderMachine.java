package io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed;

import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.AddLine;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.Cancel;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.Deliver;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.Pay;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.Place;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.Ship;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Cancelled;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Delivered;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Draft;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Paid;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Placed;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Shipped;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.Transition.Moved;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.Transition.Rejected;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The whole order lifecycle as one pure function {@code (OrderState, OrderEvent) → Transition}: a single
 * {@code switch} over the pair, with nested record patterns and {@code when} guards. No object changes; a rejected
 * event simply produces no new state.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public final class OrderMachine {

    private OrderMachine() {}

    /** The pair the transition function switches over. */
    private record Step(OrderState state, OrderEvent event) {}

    public static OrderState initial() {
        return new Draft(List.of());
    }

    public static Transition apply(OrderState state, OrderEvent event) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(event, "event");
        return switch (new Step(state, event)) {
            case Step(Draft(var lines), AddLine(var line)) ->
                    new Moved(new Draft(Stream.concat(lines.stream(), Stream.of(line)).toList()));
            case Step(Draft(var lines), Place _) when lines.isEmpty() ->
                    new Rejected("cannot place an empty order");
            case Step(Draft(var lines), Place _) -> new Moved(new Placed(lines, total(lines)));
            case Step(Placed(_, var total), Pay(_, var amount)) when amount != total ->
                    new Rejected("amount " + amount + " does not match total " + total);
            case Step(Placed(_, var total), Pay(var paymentId, _)) -> new Moved(new Paid(total, paymentId));
            case Step(Placed _, Cancel(var reason)) -> new Moved(new Cancelled(reason, false));
            case Step(Paid(_, var paymentId), Ship(var trackingNo)) -> new Moved(new Shipped(paymentId, trackingNo));
            case Step(Paid _, Cancel(var reason)) -> new Moved(new Cancelled(reason, true));
            case Step(Shipped(_, var trackingNo), Deliver _) -> new Moved(new Delivered(trackingNo));
            // Catch-all: every other pair is refused. It also silently accepts states added later (see the lesson).
            case Step(var s, var e) -> new Rejected(name(e) + " not allowed in " + name(s));
        };
    }

    /** Folds an event log into the current state, starting from {@link #initial()}; stops at the first rejection. */
    public static ReplayResult replay(List<? extends OrderEvent> log) {
        OrderState state = initial();
        for (int index = 0; index < log.size(); index++) {
            switch (apply(state, log.get(index))) {
                case Moved(var next) -> state = next;
                case Rejected(var reason) -> {
                    return new ReplayResult.Stopped(state, index, reason);
                }
            }
        }
        return new ReplayResult.Completed(state);
    }

    private static long total(List<Line> lines) {
        return lines.stream().mapToLong(Line::totalCents).reduce(0, Math::addExact);
    }

    private static String name(Object value) {
        return value.getClass().getSimpleName();
    }
}
