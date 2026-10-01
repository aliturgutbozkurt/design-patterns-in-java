package io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed;

import java.util.Objects;

/**
 * The result of one step: the order {@link Moved} to a new state, or the event was {@link Rejected} with a reason.
 * Rejection is a value, not an exception, so the caller must handle both cases.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public sealed interface Transition {

    record Moved(OrderState state) implements Transition {
        public Moved {
            Objects.requireNonNull(state, "state");
        }
    }

    record Rejected(String reason) implements Transition {
        public Rejected {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
