package io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed;

import java.util.List;
import java.util.Objects;

/**
 * State as data: each state of a PatternShop order is a record that carries only the fields that exist in that state.
 * A {@code Draft} has no tracking number, and that is a compile-time fact rather than a {@code null} check.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public sealed interface OrderState {

    /** Lines can still be added; an empty draft cannot be placed. */
    record Draft(List<Line> lines) implements OrderState {
        public Draft {
            lines = List.copyOf(lines);
        }
    }

    /** The lines are frozen and the total is known. */
    record Placed(List<Line> lines, long totalCents) implements OrderState {
        public Placed {
            lines = List.copyOf(lines);
        }
    }

    record Paid(long totalCents, String paymentId) implements OrderState {
        public Paid {
            Objects.requireNonNull(paymentId, "paymentId");
        }
    }

    record Shipped(String paymentId, String trackingNo) implements OrderState {
        public Shipped {
            Objects.requireNonNull(paymentId, "paymentId");
            Objects.requireNonNull(trackingNo, "trackingNo");
        }
    }

    record Delivered(String trackingNo) implements OrderState {
        public Delivered {
            Objects.requireNonNull(trackingNo, "trackingNo");
        }
    }

    record Cancelled(String reason, boolean refunded) implements OrderState {
        public Cancelled {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
