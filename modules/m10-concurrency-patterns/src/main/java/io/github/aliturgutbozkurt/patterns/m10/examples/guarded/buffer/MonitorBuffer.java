package io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * The classic form of Guarded Suspension: the object's own monitor, {@code synchronized}, {@code wait()} and
 * {@code notifyAll()}. There is only one wait set, so producers and consumers wait together and a state change has
 * to wake them all ({@code notifyAll}); {@code notify} could wake the wrong kind of thread and lose the signal.
 *
 * @param <T> element type
 * @see "m10 lesson, section Guarded Suspension and Balking"
 */
public final class MonitorBuffer<T> implements BoundedBuffer<T> {

    private final int capacity;
    private final Deque<T> items = new ArrayDeque<>();   // guarded by this

    public MonitorBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
    }

    @Override
    public synchronized void put(T item) throws InterruptedException {
        Objects.requireNonNull(item, "item");
        while (items.size() == capacity) {          // re-check after every wake-up (spurious or stolen)
            wait();
        }
        items.addLast(item);
        notifyAll();
    }

    @Override
    public synchronized T take() throws InterruptedException {
        while (items.isEmpty()) {
            wait();
        }
        T item = items.removeFirst();
        notifyAll();
        return item;
    }

    @Override
    public synchronized Optional<T> poll(Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (items.isEmpty()) {
            long left = deadline - System.nanoTime();
            if (left <= 0) {
                return Optional.empty();
            }
            TimeUnit.NANOSECONDS.timedWait(this, left);
        }
        T item = items.removeFirst();
        notifyAll();
        return Optional.of(item);
    }

    @Override
    public synchronized int size() {
        return items.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }
}
