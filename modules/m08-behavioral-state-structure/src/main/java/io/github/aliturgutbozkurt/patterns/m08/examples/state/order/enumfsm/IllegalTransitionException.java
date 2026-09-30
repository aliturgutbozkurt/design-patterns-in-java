package io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm;

/**
 * Thrown when an order is asked to move along an edge that the transition table does not have.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public final class IllegalTransitionException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    private final String orderId;
    private final OrderStatus from;
    private final OrderStatus to;

    public IllegalTransitionException(String orderId, OrderStatus from, OrderStatus to) {
        super("cannot move order " + orderId + " from " + from + " to " + to);
        this.orderId = orderId;
        this.from = from;
        this.to = to;
    }

    public String orderId() {
        return orderId;
    }

    public OrderStatus from() {
        return from;
    }

    public OrderStatus to() {
        return to;
    }
}
