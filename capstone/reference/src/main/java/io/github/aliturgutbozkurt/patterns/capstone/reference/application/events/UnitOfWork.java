package io.github.aliturgutbozkurt.patterns.capstone.reference.application.events;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * The transaction boundary of the shop: every change of state runs inside {@link #run}, one at a time, so a
 * read-check-write (e.g. reserving stock for several SKUs) is never interleaved with another change.
 *
 * @see "capstone guide §2 Slice walkthrough — C3"
 */
public final class UnitOfWork {

    private final ReentrantLock lock = new ReentrantLock();

    /** Runs {@code work} as one transaction and returns its result. */
    public <T> T run(Supplier<T> work) {
        lock.lock();
        try {
            return work.get();
        } finally {
            lock.unlock();
        }
    }
}
