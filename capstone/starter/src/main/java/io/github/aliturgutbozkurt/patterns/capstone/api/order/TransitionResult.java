package io.github.aliturgutbozkurt.patterns.capstone.api.order;

import java.util.Objects;

/**
 * GIVEN — do not modify. The business outcome of a lifecycle transition.
 *
 * @see "capstone brief, Business rules — Order lifecycle (F7)"
 */
public sealed interface TransitionResult {

    /**
     * The transition happened.
     *
     * @param order the order after the transition
     */
    record Done(OrderView order) implements TransitionResult {
        public Done {
            Objects.requireNonNull(order, "order");
        }
    }

    /**
     * Nothing changed.
     *
     * @param reason why, e.g. {@code cannot cancel SHIPPED order}
     */
    record Refused(String reason) implements TransitionResult {
        public Refused {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
