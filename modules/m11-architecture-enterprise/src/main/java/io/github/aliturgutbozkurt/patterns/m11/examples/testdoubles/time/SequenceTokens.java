package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.time;

import java.util.function.Supplier;

/**
 * Predictable "randomness": {@code token-1}, {@code token-2}, … — inject a {@code SecureRandom}-based supplier in
 * production, this one in tests and demos.
 *
 * @see "m11 lesson, section Test doubles — time"
 */
public final class SequenceTokens implements Supplier<String> {

    private long last;

    @Override
    public String get() {
        return "token-" + ++last;
    }
}
