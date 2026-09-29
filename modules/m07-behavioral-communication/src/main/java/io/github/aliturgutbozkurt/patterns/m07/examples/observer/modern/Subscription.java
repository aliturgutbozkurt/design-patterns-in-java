package io.github.aliturgutbozkurt.patterns.m07.examples.observer.modern;

/**
 * Handle returned when a listener subscribes; closing it unsubscribes. {@link #close()} throws no checked exception
 * and is idempotent, so it fits try-with-resources — the cure for the lapsed-listener leak.
 *
 * @see "m07 lesson, section Observer — Modern Java 27"
 */
@FunctionalInterface
public interface Subscription extends AutoCloseable {

    /** Stops delivery to the listener; calling it again does nothing. */
    @Override
    void close();
}
