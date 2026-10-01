package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.scaling;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counts how many tasks are running right now, the highest number that ever ran at the same time, and how many
 * have finished. Lock-free: every counter is an {@link AtomicInteger}.
 *
 * @see "m10 lesson, section Thread-per-task with virtual threads"
 */
public final class InFlightTracker {

    private final AtomicInteger current = new AtomicInteger();
    private final AtomicInteger peak = new AtomicInteger();
    private final AtomicInteger finished = new AtomicInteger();

    /** A task starts. */
    public void enter() {
        peak.accumulateAndGet(current.incrementAndGet(), Math::max);
    }

    /** A task ends (normally or not). */
    public void exit() {
        current.decrementAndGet();
        finished.incrementAndGet();
    }

    public int current() {
        return current.get();
    }

    public int peak() {
        return peak.get();
    }

    public int finished() {
        return finished.get();
    }
}
