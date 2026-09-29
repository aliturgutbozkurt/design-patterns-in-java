package io.github.aliturgutbozkurt.patterns.m03.examples.pool.throttle;

import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * With virtual threads, <em>threads</em> are cheap: give every task its own and do not pool them. What is scarce is
 * the downstream service — so bound <em>that</em> with a {@link Semaphore}. No objects are pooled here at all.
 *
 * @see "m03 lesson, section Object Pool — virtual threads"
 */
public final class ThrottledClient {

    private final Semaphore permits;
    private final Function<String, String> service;
    private final AtomicInteger current = new AtomicInteger();
    private final AtomicInteger peak = new AtomicInteger();

    public ThrottledClient(int maxConcurrentCalls, Function<String, String> service) {
        if (maxConcurrentCalls < 1) {
            throw new IllegalArgumentException("maxConcurrentCalls must be positive: " + maxConcurrentCalls);
        }
        this.permits = new Semaphore(maxConcurrentCalls);
        this.service = Objects.requireNonNull(service, "service");
    }

    /** Calls the service, waiting while {@code maxConcurrentCalls} other calls are in progress. */
    public String call(String request) throws InterruptedException {
        permits.acquire();
        try {
            peak.accumulateAndGet(current.incrementAndGet(), Math::max);
            return service.apply(request);
        } finally {
            current.decrementAndGet();
            permits.release();
        }
    }

    /** The highest number of calls that were in progress at the same time. */
    public int peakConcurrency() {
        return peak.get();
    }
}
