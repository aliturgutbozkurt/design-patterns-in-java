package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order;

import java.util.Objects;

/**
 * The outcome of asking the lifecycle for a transition: allowed (with the next state and the history note) or
 * refused (with the reason the customer sees).
 *
 * @see "capstone guide, Pattern map — State"
 */
public sealed interface Transition {

    /**
     * The transition may happen.
     *
     * @param next the state after it
     * @param note the history note
     */
    record Allowed(OrderState next, String note) implements Transition {
        public Allowed {
            Objects.requireNonNull(next, "next");
            Objects.requireNonNull(note, "note");
        }
    }

    /**
     * The transition may not happen; nothing changes.
     *
     * @param reason e.g. {@code cannot cancel SHIPPED order}
     */
    record Refused(String reason) implements Transition {
        public Refused {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
