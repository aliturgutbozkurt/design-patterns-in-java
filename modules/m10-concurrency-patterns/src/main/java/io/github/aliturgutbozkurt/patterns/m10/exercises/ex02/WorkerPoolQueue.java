package io.github.aliturgutbozkurt.patterns.m10.exercises.ex02;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/** Assignment 02 — your bounded job queue with workers and graceful shutdown. */
public class WorkerPoolQueue implements JobQueue {

    private final int capacity;
    private final int workers;
    private final JobHandler handler;
    private final ThreadFactory threadFactory;

    public WorkerPoolQueue(int capacity, int workers, JobHandler handler, ThreadFactory threadFactory) {
        // TODO(ex02): reject capacity < 1 or workers < 1 (IllegalArgumentException).
        // TODO(ex02): create and start exactly `workers` threads from threadFactory.
        this.capacity = capacity;
        this.workers = workers;
        this.handler = handler;
        this.threadFactory = threadFactory;
    }

    @Override
    public void submit(Job job) throws InterruptedException {
        // TODO(ex02): guarded suspension: wait while full; a shutdown releases the wait with IllegalStateException.
        throw new UnsupportedOperationException("TODO(ex02): implement submit(Job), capacity " + capacity);
    }

    @Override
    public boolean trySubmit(Job job) {
        // TODO(ex02): balking: return false at once when full or shut down.
        throw new UnsupportedOperationException("TODO(ex02): implement trySubmit(Job)");
    }

    @Override
    public void shutdown() {
        // TODO(ex02): stop accepting, wake every waiting thread, never block.
        throw new UnsupportedOperationException("TODO(ex02): implement shutdown()");
    }

    @Override
    public List<Job> shutdownNow() {
        // TODO(ex02): like shutdown, plus interrupt running handlers and return the queued jobs in order.
        throw new UnsupportedOperationException("TODO(ex02): implement shutdownNow()");
    }

    @Override
    public boolean awaitTermination(Duration timeout) throws InterruptedException {
        throw new UnsupportedOperationException("TODO(ex02): implement awaitTermination(Duration), "
                + workers + " workers");
    }

    @Override
    public List<JobOutcome> outcomes() {
        // TODO(ex02): handler result -> Completed, handler exception -> Failed; sorted by job id.
        throw new UnsupportedOperationException("TODO(ex02): implement outcomes() for " + handler);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("TODO(ex02): implement close() with " + threadFactory);
    }
}
