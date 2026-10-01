package io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed;

import java.util.Objects;

/**
 * Everything that can happen to an order, as immutable records. The machine decides whether an event is valid in
 * the current state; the event itself only validates its own fields.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public sealed interface OrderEvent {

    record AddLine(Line line) implements OrderEvent {
        public AddLine {
            Objects.requireNonNull(line, "line");
        }
    }

    record Place() implements OrderEvent {}

    record Pay(String paymentId, long amountCents) implements OrderEvent {
        public Pay {
            requireText(paymentId, "paymentId");
            if (amountCents <= 0) {
                throw new IllegalArgumentException("amount must be positive: " + amountCents);
            }
        }
    }

    record Ship(String trackingNo) implements OrderEvent {
        public Ship {
            requireText(trackingNo, "trackingNo");
        }
    }

    record Deliver() implements OrderEvent {}

    record Cancel(String reason) implements OrderEvent {
        public Cancel {
            requireText(reason, "reason");
        }
    }

    private static void requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
