package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout;

/**
 * Records declined payments (not used on the happy path: pass a dummy there).
 *
 * @see "m11 lesson, section Test doubles"
 */
@FunctionalInterface
public interface AuditLog {

    void record(String entry);
}
