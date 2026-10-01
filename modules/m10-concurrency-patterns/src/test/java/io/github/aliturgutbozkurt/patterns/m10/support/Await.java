package io.github.aliturgutbozkurt.patterns.m10.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test helper for deterministic concurrency tests (spec m10, "Deterministic concurrency tests"): every wait is
 * bounded by {@link #BOUND}, and "thread X is now blocked" is detected by polling its state, never by sleeping.
 */
public final class Await {

    /** Upper bound for every blocking wait in a test. Only a broken implementation ever gets close to it. */
    public static final Duration BOUND = Duration.ofSeconds(5);

    private Await() {}

    /** Waits (bounded) until {@code thread} is parked in {@code WAITING} or {@code TIMED_WAITING}. */
    public static void untilBlocked(Thread thread) {
        long deadline = System.nanoTime() + BOUND.toNanos();
        while (true) {
            Thread.State state = thread.getState();
            if (state == Thread.State.WAITING || state == Thread.State.TIMED_WAITING) {
                return;
            }
            if (state == Thread.State.TERMINATED) {
                throw new AssertionError(thread + " terminated instead of blocking");
            }
            if (System.nanoTime() - deadline > 0) {
                throw new AssertionError(thread + " did not block within " + BOUND + " (state " + state + ")");
            }
            Thread.onSpinWait();
        }
    }

    /** Waits (bounded) for {@code latch} and asserts that it really opened. */
    public static void latch(CountDownLatch latch) throws InterruptedException {
        assertThat(latch.await(BOUND.toMillis(), TimeUnit.MILLISECONDS))
                .as("latch opened within %s (count left: %d)", BOUND, latch.getCount())
                .isTrue();
    }

    /** Waits (bounded) for {@code thread} to end and asserts that it did. */
    public static void terminated(Thread thread) throws InterruptedException {
        assertThat(thread.join(BOUND)).as("%s terminated within %s", thread, BOUND).isTrue();
    }
}
