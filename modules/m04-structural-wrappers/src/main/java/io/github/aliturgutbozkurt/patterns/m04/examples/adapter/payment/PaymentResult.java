package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment;

import java.util.Objects;

/**
 * The outcome of a payment: a closed set, so clients handle it with an exhaustive {@code switch}.
 *
 * @see "m04 lesson, section Adapter"
 */
public sealed interface PaymentResult {

    /** The payment went through. */
    record Approved(String transactionId) implements PaymentResult {
        public Approved {
            Objects.requireNonNull(transactionId, "transactionId");
        }
    }

    /** The payment was refused. */
    record Declined(DeclineReason reason, String message) implements PaymentResult {
        public Declined {
            Objects.requireNonNull(reason, "reason");
            Objects.requireNonNull(message, "message");
        }
    }
}
