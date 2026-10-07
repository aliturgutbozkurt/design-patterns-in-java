package io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out;

import java.util.Objects;

/**
 * The business outcome of a payment call.
 *
 * @see "capstone guide, Pattern map — Adapter"
 */
public sealed interface PaymentOutcome {

    /**
     * The provider approved.
     *
     * @param reference the provider's reference
     */
    record Approved(String reference) implements PaymentOutcome {
        public Approved {
            Objects.requireNonNull(reference, "reference");
        }
    }

    /**
     * The provider declined (e.g. the card).
     *
     * @param message the provider's message
     */
    record Declined(String message) implements PaymentOutcome {
        public Declined {
            Objects.requireNonNull(message, "message");
        }
    }

    /**
     * The provider could not be reached or failed.
     *
     * @param message the provider's message
     */
    record Unavailable(String message) implements PaymentOutcome {
        public Unavailable {
            Objects.requireNonNull(message, "message");
        }
    }
}
