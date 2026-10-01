package io.github.aliturgutbozkurt.patterns.m11.exercises.ex02;

/** GIVEN — do not modify. Returned by {@code subscribe}; {@code close()} stops delivery and is idempotent. */
@FunctionalInterface
public interface Subscription {

    void close();
}
