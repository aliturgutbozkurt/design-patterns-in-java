package io.github.aliturgutbozkurt.patterns.m07.exercises.ex01;

/** GIVEN — do not modify. Returned by {@code subscribe}; closing it unsubscribes. Must be idempotent. */
@FunctionalInterface
public interface Subscription extends AutoCloseable {

    @Override
    void close();
}
