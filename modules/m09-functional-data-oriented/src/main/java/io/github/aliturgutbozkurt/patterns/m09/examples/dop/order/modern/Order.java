package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern;

import java.util.List;
import java.util.Objects;

/**
 * Data-oriented order: one record per state, each carrying only the fields that make sense in that state. "Shipped
 * without a tracking code" or "cancelled but shipped" cannot be built at all.
 *
 * @see "m09 lesson, section Data-oriented programming — Modern Java 27"
 */
public sealed interface Order permits Order.Draft, Order.Placed, Order.Paid, Order.Shipped, Order.Cancelled {

    OrderId id();

    List<OrderLine> lines();

    /** Being edited; may still be empty. */
    record Draft(OrderId id, List<OrderLine> lines) implements Order {
        public Draft {
            Objects.requireNonNull(id, "id");
            lines = List.copyOf(lines);
        }
    }

    /** Submitted by the customer, waiting for payment. */
    record Placed(OrderId id, List<OrderLine> lines) implements Order {
        public Placed {
            Objects.requireNonNull(id, "id");
            lines = nonEmpty(lines, "a placed order");
        }
    }

    record Paid(OrderId id, List<OrderLine> lines, String paymentRef) implements Order {
        public Paid {
            Objects.requireNonNull(id, "id");
            lines = nonEmpty(lines, "a paid order");
            requireText(paymentRef, "paymentRef");
        }
    }

    record Shipped(OrderId id, List<OrderLine> lines, String paymentRef, String trackingCode) implements Order {
        public Shipped {
            Objects.requireNonNull(id, "id");
            lines = nonEmpty(lines, "a shipped order");
            requireText(paymentRef, "paymentRef");
            requireText(trackingCode, "trackingCode");
        }
    }

    /** Cancelled before shipping; {@code refundDue} is true when the customer had already paid. */
    record Cancelled(OrderId id, List<OrderLine> lines, String reason, boolean refundDue) implements Order {
        public Cancelled {
            Objects.requireNonNull(id, "id");
            lines = List.copyOf(lines);
            requireText(reason, "reason");
        }
    }

    private static List<OrderLine> nonEmpty(List<OrderLine> lines, String what) {
        List<OrderLine> copy = List.copyOf(lines);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException(what + " needs at least one line");
        }
        return copy;
    }

    private static void requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
