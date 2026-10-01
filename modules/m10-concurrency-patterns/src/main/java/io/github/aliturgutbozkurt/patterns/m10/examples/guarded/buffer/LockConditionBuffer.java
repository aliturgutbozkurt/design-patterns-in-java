package io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Guarded Suspension with {@link ReentrantLock} and two named {@link Condition}s. Producers wait on {@code notFull},
 * consumers on {@code notEmpty}, so a {@code put} wakes a consumer and never another producer. Every guard is a
 * {@code while} loop, because a woken thread must check the condition again.
 *
 * @param <T> element type
 * @see "m10 lesson, section Guarded Suspension and Balking"
 */
public final class LockConditionBuffer<T> implements BoundedBuffer<T> {

    private final int capacity;
    private final Deque<T> items = new ArrayDeque<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public LockConditionBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
    }

    @Override
    public void put(T item) throws InterruptedException {
        Objects.requireNonNull(item, "item");
        lock.lockInterruptibly();
        try {
            while (items.size() == capacity) {      // guard: while, never if
                notFull.await();
            }
            items.addLast(item);
            notEmpty.signal();                      // wake one consumer: only consumers wait on notEmpty
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (items.isEmpty()) {
                notEmpty.await();
            }
            return removeFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<T> poll(Duration timeout) throws InterruptedException {
        long nanos = timeout.toNanos();
        lock.lockInterruptibly();
        try {
            while (items.isEmpty()) {
                if (nanos <= 0) {
                    return Optional.empty();
                }
                nanos = notEmpty.awaitNanos(nanos); // returns the time that is left
            }
            return Optional.of(removeFirst());
        } finally {
            lock.unlock();
        }
    }

    private T removeFirst() {
        T item = items.removeFirst();
        notFull.signal();                           // wake one producer
        return item;
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return items.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int capacity() {
        return capacity;
    }
}
