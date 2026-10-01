package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout;

import java.util.Optional;

/**
 * Stores receipts (state: replace it with a fake and inspect the state afterwards).
 *
 * @see "m11 lesson, section Test doubles"
 */
public interface ReceiptRepository {

    void save(Receipt receipt);

    Optional<Receipt> findById(String id);
}
