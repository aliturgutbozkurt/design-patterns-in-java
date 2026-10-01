package io.github.aliturgutbozkurt.patterns.m11.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. The domain events of the order lifecycle. */
public sealed interface OrderEvent {

    OrderId id();

    record OrderPlaced(OrderId id, long totalCents) implements OrderEvent {
        public OrderPlaced {
            Objects.requireNonNull(id, "id");
        }
    }

    record OrderPaid(OrderId id) implements OrderEvent {
        public OrderPaid {
            Objects.requireNonNull(id, "id");
        }
    }

    record OrderShipped(OrderId id) implements OrderEvent {
        public OrderShipped {
            Objects.requireNonNull(id, "id");
        }
    }

    record OrderCancelled(OrderId id, String reason) implements OrderEvent {
        public OrderCancelled {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(reason, "reason");
        }
    }
}
