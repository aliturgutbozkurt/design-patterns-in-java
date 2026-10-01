package io.github.aliturgutbozkurt.patterns.m08.examples.state.jdk;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * Observes the JDK's own state enum, {@link Thread.State}, on platform and virtual threads. Every observation waits
 * for the state it expects (bounded polling) instead of sleeping, so the results are deterministic.
 *
 * @see "m08 lesson, section State — Real-world usage"
 */
public final class ThreadStates {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private ThreadStates() {}

    /** Starts a thread that parks on {@code gate} until it is counted down: it reports {@code WAITING}. */
    public static Thread startParked(Thread.Builder builder, CountDownLatch gate) {
        Objects.requireNonNull(gate, "gate");
        return builder.start(() -> {
            try {
                gate.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // keep the flag; the thread just ends
            }
        });
    }

    /** Starts a thread that sleeps for a minute unless interrupted: it reports {@code TIMED_WAITING}. */
    public static Thread startSleeping(Thread.Builder builder) {
        return builder.start(ThreadStates::sleepUntilInterrupted);
    }

    /**
     * Polls {@code thread.getState()} until it equals {@code expected}, for at most {@code timeout}.
     *
     * @return {@code expected}
     * @throws IllegalStateException if the state was not reached in time
     */
    public static Thread.State awaitState(Thread thread, Thread.State expected, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (thread.getState() != expected) {
            if (System.nanoTime() - deadline > 0) {
                throw new IllegalStateException("thread did not reach " + expected + " within " + timeout
                        + " (still " + thread.getState() + ")");
            }
            Thread.onSpinWait();
        }
        return expected;
    }

    /**
     * Drives one thread through {@code NEW → WAITING → TIMED_WAITING → TERMINATED} and returns what it observed.
     * {@code RUNNABLE} and {@code BLOCKED} are left out: they are too short-lived to observe without a race.
     */
    public static List<Thread.State> lifecycle(Thread.Builder builder) throws InterruptedException {
        var gate = new CountDownLatch(1);
        Thread thread = builder.unstarted(() -> {
            try {
                gate.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            sleepUntilInterrupted();
        });
        var observed = new ArrayList<Thread.State>();
        observed.add(thread.getState());
        thread.start();
        observed.add(awaitState(thread, Thread.State.WAITING, TIMEOUT));
        gate.countDown();
        observed.add(awaitState(thread, Thread.State.TIMED_WAITING, TIMEOUT));
        thread.interrupt();
        thread.join();
        observed.add(thread.getState());
        return List.copyOf(observed);
    }

    private static void sleepUntilInterrupted() {
        try {
            Thread.sleep(Duration.ofMinutes(1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // the interrupt is the planned wake-up; keep the flag
        }
    }
}
