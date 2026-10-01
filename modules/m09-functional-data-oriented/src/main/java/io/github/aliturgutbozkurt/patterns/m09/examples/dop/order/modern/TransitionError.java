package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern;

/**
 * Why a transition was refused. A sealed type, so callers handle every reason exhaustively.
 *
 * @see "m09 lesson, section Data-oriented programming — Modern Java 27"
 */
public sealed interface TransitionError
        permits TransitionError.EmptyOrder, TransitionError.AlreadyShipped, TransitionError.AlreadyCancelled {

    OrderId id();

    record EmptyOrder(OrderId id) implements TransitionError {}

    record AlreadyShipped(OrderId id) implements TransitionError {}

    record AlreadyCancelled(OrderId id) implements TransitionError {}
}
