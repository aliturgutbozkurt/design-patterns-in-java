package io.github.aliturgutbozkurt.patterns.m03.examples.pool;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

/**
 * Object Pool: creates at most {@code size} connections, lazily, and lends them out one caller at a time. A
 * {@link Semaphore} bounds how many leases exist; idle connections wait in a deque for the next caller.
 *
 * @see "m03 lesson, section Object Pool"
 */
public final class ConnectionPool {

    private final Semaphore permits;
    private final IntFunction<Connection> factory;
    private final Deque<Connection> idle = new ArrayDeque<>();
    private int created;
    private int inUse;
    private int maxInUse;

    /** @param factory creates connection number {@code n} (1-based) */
    public ConnectionPool(int size, IntFunction<Connection> factory) {
        if (size < 1) {
            throw new IllegalArgumentException("size must be positive: " + size);
        }
        this.permits = new Semaphore(size, true);
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    /**
     * Borrows a connection, waiting at most {@code timeout}. Use it in try-with-resources so it always goes back.
     *
     * @throws IllegalStateException if no connection became free in time
     */
    public PooledConnection acquire(Duration timeout) throws InterruptedException {
        if (!permits.tryAcquire(timeout.toNanos(), TimeUnit.NANOSECONDS)) {
            throw new IllegalStateException("no connection available within " + timeout);
        }
        Connection connection;
        synchronized (this) {
            connection = idle.pollFirst();
            if (connection == null) {
                try {
                    connection = factory.apply(created + 1);
                } catch (RuntimeException e) {
                    permits.release();                          // a failed creation must not shrink the pool
                    throw e;
                }
                created++;
            }
            inUse++;
            maxInUse = Math.max(maxInUse, inUse);
        }
        return new PooledConnection(this, connection);
    }

    void release(Connection connection) {
        synchronized (this) {
            inUse--;
            idle.addFirst(connection);
        }
        permits.release();
    }

    /** How many connections were ever created. */
    public synchronized int created() {
        return created;
    }

    public synchronized int inUse() {
        return inUse;
    }

    /** The highest number of simultaneous leases seen so far. */
    public synchronized int maxInUse() {
        return maxInUse;
    }
}
