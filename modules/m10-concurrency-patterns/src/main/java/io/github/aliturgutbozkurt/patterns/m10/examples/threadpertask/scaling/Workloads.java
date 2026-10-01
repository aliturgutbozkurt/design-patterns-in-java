package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.scaling;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The same blocking workload run two ways. A fixed pool caps how many tasks can wait at once at its thread count;
 * one virtual thread per task removes that cap, because a blocked virtual thread costs almost nothing.
 *
 * @see "m10 lesson, section Thread-per-task with virtual threads"
 */
public final class Workloads {

    /** How long a job waits at its gate before it gives up (only a broken setup ever gets there). */
    static final Duration GATE_TIMEOUT = Duration.ofSeconds(5);

    /** What a run measured. */
    public record Result(int completed, int peakInFlight) {}

    private Workloads() {}

    /** Runs {@code tasks} blocking jobs on a fixed pool of {@code threads} platform threads. */
    public static Result runOnFixedPool(int threads, int tasks) {
        var tracker = new InFlightTracker();
        var gate = new CountDownLatch(Math.min(threads, tasks));    // opens when the pool is fully busy
        List<Future<?>> futures;
        try (var pool = Executors.newFixedThreadPool(threads, Thread.ofPlatform().name("worker-", 0).factory())) {
            futures = submitAll(pool, tasks, tracker, gate);
        }                                                           // close() waits for every task
        return result(tracker, futures);
    }

    /** Runs {@code tasks} blocking jobs, each on its own new virtual thread. */
    public static Result runThreadPerTask(int tasks) {
        var tracker = new InFlightTracker();
        var gate = new CountDownLatch(tasks);                       // opens only when ALL tasks are in flight
        List<Future<?>> futures;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            futures = submitAll(executor, tasks, tracker, gate);
        }
        return result(tracker, futures);
    }

    private static List<Future<?>> submitAll(ExecutorService executor, int tasks, InFlightTracker tracker,
            CountDownLatch gate) {
        List<Future<?>> futures = new ArrayList<>(tasks);
        for (int i = 0; i < tasks; i++) {
            futures.add(executor.submit(new BlockingJob(tracker, gate, GATE_TIMEOUT)));
        }
        return futures;
    }

    private static Result result(InFlightTracker tracker, List<Future<?>> futures) {
        for (Future<?> future : futures) {
            if (future.state() == Future.State.FAILED) {            // do not swallow a failed job
                throw new IllegalStateException("a job failed", future.exceptionNow());
            }
        }
        return new Result(tracker.finished(), tracker.peak());
    }
}
