package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. Every business outcome of a checkout. */
public sealed interface CheckoutResult {

    /** Charged, saved and published. */
    record Confirmed(Order order) implements CheckoutResult {
        public Confirmed {
            Objects.requireNonNull(order, "order");
        }
    }

    /** Nothing was charged, saved or published — except for {@code "payment declined"}, where the charge was tried. */
    record Rejected(String reason) implements CheckoutResult {
        public Rejected {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
