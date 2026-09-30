package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.scaling;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A task that blocks like an I/O call, but deterministically: it waits at a shared gate that opens once the gate's
 * count of jobs have arrived. Many jobs can only pass together if they really are in flight together, so the
 * measured peak does not depend on timing or luck.
 *
 * @see "m10 lesson, section Thread-per-task with virtual threads"
 */
public final class BlockingJob implements Runnable {

    private final InFlightTracker tracker;
    private final CountDownLatch gate;
    private final Duration maxWait;

    public BlockingJob(InFlightTracker tracker, CountDownLatch gate, Duration maxWait) {
        this.tracker = Objects.requireNonNull(tracker, "tracker");
        this.gate = Objects.requireNonNull(gate, "gate");
        this.maxWait = Objects.requireNonNull(maxWait, "maxWait");
    }

    @Override
    public void run() {
        tracker.enter();
        try {
            gate.countDown();
            if (!gate.await(maxWait.toNanos(), TimeUnit.NANOSECONDS)) {   // bounded: never hangs
                throw new IllegalStateException("gate still closed after " + maxWait
                        + ": fewer jobs than expected were in flight together");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while waiting at the gate", e);
        } finally {
            tracker.exit();
        }
    }
}
