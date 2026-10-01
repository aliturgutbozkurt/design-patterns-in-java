package io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer;

import java.time.Duration;
import java.util.Optional;

/**
 * Guarded Suspension: a hand-off buffer whose operations wait until their precondition holds. {@code put} waits
 * while the buffer is full, {@code take} waits while it is empty. Waiting is interruptible and can be timed.
 *
 * @param <T> element type
 * @see "m10 lesson, section Guarded Suspension and Balking"
 */
public interface BoundedBuffer<T> {

    /** Adds {@code item}, waiting while the buffer is full. */
    void put(T item) throws InterruptedException;

    /** Removes the oldest element, waiting while the buffer is empty. */
    T take() throws InterruptedException;

    /** Like {@link #take()}, but gives up after {@code timeout} and returns {@link Optional#empty()}. */
    Optional<T> poll(Duration timeout) throws InterruptedException;

    int size();

    int capacity();
}
