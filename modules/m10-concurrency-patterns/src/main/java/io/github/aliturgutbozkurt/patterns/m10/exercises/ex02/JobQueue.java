package io.github.aliturgutbozkurt.patterns.m10.exercises.ex02;

import java.time.Duration;
import java.util.List;

/**
 * GIVEN — do not modify. A bounded job queue served by a fixed number of worker threads: Producer–Consumer whose
 * blocking rules are Guarded Suspension ({@link #submit}) and Balking ({@link #trySubmit}).
 */
public interface JobQueue extends AutoCloseable {

    /** Queues {@code job}, waiting while the queue is full. Throws {@link IllegalStateException} once shut down. */
    void submit(Job job) throws InterruptedException;

    /** Queues {@code job} if there is room right now; {@code false} when the queue is full or shut down. */
    boolean trySubmit(Job job);

    /** Stops accepting jobs; queued jobs still run. Never blocks; releases blocked submitters. Idempotent. */
    void shutdown();

    /** Stops accepting jobs, interrupts running handlers, returns the queued jobs that never started. */
    List<Job> shutdownNow();

    /** {@code true} once every worker has exited, {@code false} if {@code timeout} elapsed first. */
    boolean awaitTermination(Duration timeout) throws InterruptedException;

    /** An immutable snapshot of the outcomes so far, sorted by job id. */
    List<JobOutcome> outcomes();

    /** {@link #shutdown()}, then waits for termination (if interrupted: restores the flag and returns). */
    @Override
    void close();
}
