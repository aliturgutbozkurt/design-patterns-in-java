package io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed;

import java.util.Objects;

/**
 * The result of folding an event log: every event was applied, or the replay stopped at the first rejected event
 * (its index in the log) and kept the last good state.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public sealed interface ReplayResult {

    record Completed(OrderState state) implements ReplayResult {
        public Completed {
            Objects.requireNonNull(state, "state");
        }
    }

    record Stopped(OrderState state, int index, String reason) implements ReplayResult {
        public Stopped {
            Objects.requireNonNull(state, "state");
            Objects.requireNonNull(reason, "reason");
        }
    }
}
