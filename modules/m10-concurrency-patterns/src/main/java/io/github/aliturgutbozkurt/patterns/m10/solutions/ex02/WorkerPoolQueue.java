package io.github.aliturgutbozkurt.patterns.m10.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m10.exercises.ex02.Job;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex02.JobHandler;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex02.JobOutcome;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex02.JobQueue;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Reference solution for assignment 02. One {@link ReentrantLock} guards the queue and the {@code shutdown} flag;
 * three conditions name what threads wait for: {@code notFull} (submitters), {@code notEmpty} (workers) and
 * {@code terminated} ({@code awaitTermination}). Every guard is a {@code while} loop that also checks
 * {@code shutdown}, which is how a shutdown releases a blocked submitter: a poison pill in a plain
 * {@code ArrayBlockingQueue} could not, because the pill itself would have to wait for space.
 *
 * @see "m10 lesson, section Guarded Suspension and Balking — ex02"
 */
public final class WorkerPoolQueue implements JobQueue {

    private final int capacity;
    private final JobHandler handler;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final Condition terminated = lock.newCondition();
    private final Deque<Job> queue = new ArrayDeque<>();        // guarded by lock
    private boolean shutdown;                                    // guarded by lock
    private int liveWorkers;                                     // guarded by lock
    private final List<Thread> workers = new ArrayList<>();
    private final Queue<JobOutcome> outcomes = new ConcurrentLinkedQueue<>();

    public WorkerPoolQueue(int capacity, int workers, JobHandler handler, ThreadFactory threadFactory) {
        if (capacity < 1 || workers < 1) {
            throw new IllegalArgumentException("capacity and workers must be positive: " + capacity + ", " + workers);
        }
        this.capacity = capacity;
        this.handler = Objects.requireNonNull(handler, "handler");
        this.liveWorkers = workers;
        for (int i = 0; i < workers; i++) {
            this.workers.add(threadFactory.newThread(this::work));
        }
        this.workers.forEach(Thread::start);
    }

    @Override
    public void submit(Job job) throws InterruptedException {
        Objects.requireNonNull(job, "job");
        lock.lockInterruptibly();
        try {
            while (queue.size() == capacity && !shutdown) {      // guarded suspension
                notFull.await();
            }
            if (shutdown) {
                throw new IllegalStateException("queue is shut down");
            }
            queue.addLast(job);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean trySubmit(Job job) {
        Objects.requireNonNull(job, "job");
        lock.lock();
        try {
            if (shutdown || queue.size() == capacity) {
                return false;                                    // balk
            }
            queue.addLast(job);
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            shutdown = true;
            notFull.signalAll();                                 // blocked submitters re-check and throw
            notEmpty.signalAll();                                // idle workers re-check and exit when drained
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Job> shutdownNow() {
        List<Job> neverStarted;
        lock.lock();
        try {
            shutdown = true;
            neverStarted = List.copyOf(queue);
            queue.clear();
            notFull.signalAll();
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
        workers.forEach(Thread::interrupt);                      // running handlers see the interrupt
        return neverStarted;
    }

    @Override
    public boolean awaitTermination(Duration timeout) throws InterruptedException {
        long nanos = timeout.toNanos();
        lock.lockInterruptibly();
        try {
            while (liveWorkers > 0) {
                if (nanos <= 0) {
                    return false;
                }
                nanos = terminated.awaitNanos(nanos);
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<JobOutcome> outcomes() {
        return outcomes.stream().sorted(Comparator.comparingLong(JobOutcome::jobId)).toList();
    }

    @Override
    public void close() {
        shutdown();
        lock.lock();
        try {
            while (liveWorkers > 0) {                            // close() has no timeout
                terminated.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();                  // give up waiting, keep the flag
        } finally {
            lock.unlock();
        }
    }

    private void work() {
        try {
            Job job;
            while ((job = next()) != null) {
                run(job);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();                  // shutdownNow while idle: just exit
        } finally {
            lock.lock();
            try {
                if (--liveWorkers == 0) {
                    terminated.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /** The next job, or {@code null} once the queue is shut down and drained. */
    private Job next() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (queue.isEmpty() && !shutdown) {
                notEmpty.await();
            }
            if (queue.isEmpty()) {
                return null;
            }
            Job job = queue.removeFirst();
            notFull.signal();
            return job;
        } finally {
            lock.unlock();
        }
    }

    private void run(Job job) {
        try {
            outcomes.add(new JobOutcome.Completed(job.id(), handler.handle(job)));
        } catch (InterruptedException e) {
            outcomes.add(new JobOutcome.Failed(job.id(), "interrupted"));
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            outcomes.add(new JobOutcome.Failed(job.id(), String.valueOf(e.getMessage())));
        }
    }
}
