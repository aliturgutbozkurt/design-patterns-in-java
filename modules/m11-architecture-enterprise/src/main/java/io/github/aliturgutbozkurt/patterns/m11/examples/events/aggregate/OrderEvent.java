package io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate;

import java.util.Objects;

/**
 * Domain events of the order lifecycle: immutable records in a sealed hierarchy, named in the past tense — they
 * describe something that already happened.
 *
 * @see "m11 lesson, section Domain events"
 */
public sealed interface OrderEvent {

    /** The order the event is about. */
    String orderId();

    record OrderPlaced(String orderId, long totalCents) implements OrderEvent {
        public OrderPlaced {
            Objects.requireNonNull(orderId, "orderId");
        }
    }

    record OrderPaid(String orderId) implements OrderEvent {
        public OrderPaid {
            Objects.requireNonNull(orderId, "orderId");
        }
    }

    record OrderShipped(String orderId) implements OrderEvent {
        public OrderShipped {
            Objects.requireNonNull(orderId, "orderId");
        }
    }

    record OrderCancelled(String orderId, String reason) implements OrderEvent {
        public OrderCancelled {
            Objects.requireNonNull(orderId, "orderId");
            Objects.requireNonNull(reason, "reason");
        }
    }
}
