package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout;

import java.util.Optional;

/**
 * Charges money (a command with an important side effect: verify it with a mock).
 *
 * @see "m11 lesson, section Test doubles"
 */
@FunctionalInterface
public interface PaymentGateway {

    /** The transaction id, or empty if the charge was declined. */
    Optional<String> charge(String customer, long cents);
}
