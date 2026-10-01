package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import java.util.Objects;

/**
 * Proof of a successful checkout.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public record Receipt(String paymentId, long totalCents) {

    public Receipt {
        Objects.requireNonNull(paymentId, "paymentId");
    }
}
