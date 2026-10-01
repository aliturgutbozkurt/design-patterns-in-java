package io.github.aliturgutbozkurt.patterns.m10.examples.guarded.gate;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Guarded Suspension on a <em>state</em>: callers suspend until the service is {@link State#READY}. The state moves
 * once, from {@code STARTING} to {@code READY} or {@code FAILED}. A failed start wakes every waiter with the cause,
 * so no caller is left hanging.
 *
 * @see "m10 lesson, section Guarded Suspension and Balking"
 */
public final class ReadinessGate {

    /** The life cycle of the service behind the gate. */
    public enum State { STARTING, READY, FAILED }

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition settled = lock.newCondition();
    private State state = State.STARTING;       // guarded by lock
    private Throwable failure;                  // guarded by lock

    public State state() {
        lock.lock();
        try {
            return state;
        } finally {
            lock.unlock();
        }
    }

    /** {@code STARTING → READY}; wakes every waiter. */
    public void markReady() {
        settle(State.READY, null);
    }

    /** {@code STARTING → FAILED}; every waiter (now and later) gets an exception caused by {@code cause}. */
    public void markFailed(Throwable cause) {
        settle(State.FAILED, Objects.requireNonNull(cause, "cause"));
    }

    /**
     * Waits until the service is ready. Returns {@code true} when it is, {@code false} if {@code timeout} elapsed
     * first, and throws {@link IllegalStateException} if the start-up failed.
     */
    public boolean awaitReady(Duration timeout) throws InterruptedException {
        long nanos = timeout.toNanos();
        lock.lockInterruptibly();
        try {
            while (state == State.STARTING) {           // the guard
                if (nanos <= 0) {
                    return false;
                }
                nanos = settled.awaitNanos(nanos);
            }
            if (state == State.FAILED) {
                throw new IllegalStateException("service failed to start", failure);
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    private void settle(State next, Throwable cause) {
        lock.lock();
        try {
            if (state != State.STARTING) {
                throw new IllegalStateException("illegal transition " + state + " -> " + next);
            }
            state = next;
            failure = cause;
            settled.signalAll();                        // every waiter must re-check the guard
        } finally {
            lock.unlock();
        }
    }
}
