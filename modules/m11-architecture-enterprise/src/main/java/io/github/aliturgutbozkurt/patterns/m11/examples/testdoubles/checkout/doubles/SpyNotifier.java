package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles;

import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.Notifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Spy: does nothing useful, but records every call with its arguments; the test asserts on the recording afterwards.
 *
 * @see "m11 lesson, section Test doubles"
 */
public final class SpyNotifier implements Notifier {

    private final List<String> calls = new ArrayList<>();

    @Override
    public void notify(String customer, String message) {
        calls.add(customer + ": " + message);
    }

    /** Every call as {@code "<customer>: <message>"}, in order (an unmodifiable copy). */
    public List<String> calls() {
        return List.copyOf(calls);
    }
}
