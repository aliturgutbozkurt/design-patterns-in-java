package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles;

import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.PaymentGateway;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * Mock: programmed with expectations <em>before</em> the call, fails fast on an unexpected call, and
 * {@link #verify()} fails if an expected call never happened — interaction verification, written by hand.
 *
 * @see "m11 lesson, section Test doubles"
 */
public final class MockPaymentGateway implements PaymentGateway {

    private record Expectation(String customer, long cents, boolean approve) {
        @Override
        public String toString() {
            return customer + " " + cents;
        }
    }

    private final Deque<Expectation> expected = new ArrayDeque<>();
    private int transactions;

    /** Expects {@code customer} to be charged {@code cents}, and approves it. */
    public MockPaymentGateway expectCharge(String customer, long cents) {
        expected.addLast(new Expectation(customer, cents, true));
        return this;
    }

    /** Expects {@code customer} to be charged {@code cents}, and declines it. */
    public MockPaymentGateway expectDeclinedCharge(String customer, long cents) {
        expected.addLast(new Expectation(customer, cents, false));
        return this;
    }

    @Override
    public Optional<String> charge(String customer, long cents) {
        Expectation next = expected.peekFirst();
        if (next == null || !next.customer().equals(customer) || next.cents() != cents) {
            throw new AssertionError("unexpected charge " + customer + " " + cents + ", expected "
                    + (next == null ? "no charge" : next));
        }
        expected.removeFirst();
        return next.approve() ? Optional.of("TX-" + ++transactions) : Optional.empty();
    }

    /** Fails if an expected charge was never made. */
    public void verify() {
        if (!expected.isEmpty()) {
            throw new AssertionError("missing expected charge: " + expected.peekFirst());
        }
    }
}
