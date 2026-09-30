package io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A PatternShop order whose lifecycle is guarded by {@link OrderStatus}: every move is checked against the table,
 * and a refused move changes neither the status nor the history.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public final class Order {

    /** One accepted move, printed as {@code NEW -> PAID}. */
    public record Transition(OrderStatus from, OrderStatus to) {

        public Transition {
            Objects.requireNonNull(from, "from");
            Objects.requireNonNull(to, "to");
        }

        @Override
        public String toString() {
            return from + " -> " + to;
        }
    }

    private final String id;
    private final List<Transition> history = new ArrayList<>();
    private OrderStatus status = OrderStatus.NEW;

    public Order(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public String id() {
        return id;
    }

    public OrderStatus status() {
        return status;
    }

    /** Moves to {@code target} or throws {@link IllegalTransitionException} without changing anything. */
    public void moveTo(OrderStatus target) {
        Objects.requireNonNull(target, "target");
        if (!status.canMoveTo(target)) {
            throw new IllegalTransitionException(id, status, target);
        }
        history.add(new Transition(status, target));
        status = target;
    }

    /** Accepted moves, oldest first (read-only). */
    public List<Transition> history() {
        return List.copyOf(history);
    }
}
