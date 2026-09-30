package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout;

import java.util.List;
import java.util.Objects;

/**
 * Proof of a successful checkout.
 *
 * @param id the payment transaction id
 * @param customer who paid
 * @param skus what was bought (copied)
 * @param totalCents what was charged
 * @see "m11 lesson, section Test doubles"
 */
public record Receipt(String id, String customer, List<String> skus, long totalCents) {

    public Receipt {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(customer, "customer");
        skus = List.copyOf(skus);
    }
}
