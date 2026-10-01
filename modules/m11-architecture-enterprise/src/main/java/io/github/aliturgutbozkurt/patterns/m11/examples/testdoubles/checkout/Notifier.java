package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout;

/**
 * Tells the customer (an outgoing message: record it with a spy).
 *
 * @see "m11 lesson, section Test doubles"
 */
@FunctionalInterface
public interface Notifier {

    void notify(String customer, String message);
}
