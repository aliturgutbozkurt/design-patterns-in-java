package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles;

import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.AuditLog;

/**
 * Dummy: fills a parameter that the test path must never use — and fails loudly if it is used after all.
 *
 * @see "m11 lesson, section Test doubles"
 */
public final class DummyAuditLog implements AuditLog {

    @Override
    public void record(String entry) {
        throw new AssertionError("the audit log must not be used here, but got: " + entry);
    }
}
