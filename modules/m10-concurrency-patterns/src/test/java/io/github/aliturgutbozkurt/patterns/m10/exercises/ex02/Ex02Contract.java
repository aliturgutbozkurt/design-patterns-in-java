package io.github.aliturgutbozkurt.patterns.m10.exercises.ex02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.support.Await;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Assignment 02 — bounded job queue with graceful shutdown. Each test is one acceptance criterion of the brief.
 * Handlers that must "keep running" wait at a gate the test opens, so no test depends on timing.
 */
@Timeout(10)
public abstract class Ex02Contract {

    protected abstract JobQueue newQueue(int capacity, int workers, JobHandler handler, ThreadFactory threadFactory);

    private final List<JobQueue> queues = new ArrayList<>();
    private final CountDownLatch gate = new CountDownLatch(1);
    private final CountDownLatch entered = new CountDownLatch(1);
    private final AtomicInteger threadsCreated = new AtomicInteger();

    private final ThreadFactory countingFactory = task -> {
        threadsCreated.incrementAndGet();
        return Thread.ofVirtual().unstarted(task);
    };

    /** A handler that signals {@code entered} and then waits at the gate. */
    private final JobHandler gated = job -> {
        entered.countDown();
        Await.latch(gate);
        return "done " + job.id();
    };

    private JobQueue queue(int capacity, int workers, JobHandler handler) {
        JobQueue queue = newQueue(capacity, workers, handler, countingFactory);
        queues.add(queue);
        return queue;
    }

    private static Job job(long id) {
        return new Job(id, "payload-" + id);
    }

    @AfterEach
    void openTheGateAndStopEveryQueue() throws InterruptedException {
        gate.countDown();
        for (JobQueue queue : queues) {
            queue.shutdownNow();
            assertThat(queue.awaitTermination(Await.BOUND)).as("queue terminated").isTrue();
        }
    }

    @Test
    void processesEverySubmittedJobExactlyOnce() throws InterruptedException {
        Map<Long, AtomicInteger> runs = new ConcurrentHashMap<>();
        var queue = queue(4, 3, job -> {
            runs.computeIfAbsent(job.id(), _ -> new AtomicInteger()).incrementAndGet();
            return "ok";
        });
        for (long id = 1; id <= 100; id++) {
            queue.submit(job(id));
        }
        queue.shutdown();
        assertThat(queue.awaitTermination(Await.BOUND)).isTrue();
        assertThat(runs).hasSize(100);
        assertThat(runs.values()).allMatch(count -> count.get() == 1);
        assertThat(queue.outcomes()).hasSize(100);
    }

    @Test
    void outcomesAreSortedByJobId() throws InterruptedException {
        var queue = queue(8, 4, job -> "r" + job.id());
        for (long id : new long[] {5, 3, 9, 1, 7}) {
            queue.submit(job(id));
        }
        queue.close();
        assertThat(queue.outcomes()).extracting(JobOutcome::jobId).containsExactly(1L, 3L, 5L, 7L, 9L);
        assertThat(queue.outcomes()).first().isEqualTo(new JobOutcome.Completed(1, "r1"));
        assertThatThrownBy(() -> queue.outcomes().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void failingJobBecomesFailedOutcomeAndWorkerContinues() throws InterruptedException {
        var queue = queue(4, 1, job -> {
            if (job.id() == 1) {
                throw new IllegalStateException("boom");
            }
            return "ok " + job.id();
        });
        queue.submit(job(1));
        queue.submit(job(2));
        queue.close();
        assertThat(queue.outcomes()).containsExactly(new JobOutcome.Failed(1, "boom"),
                new JobOutcome.Completed(2, "ok 2"));
    }

    @Test
    void createsExactlyTheConfiguredNumberOfWorkers() throws InterruptedException {
        var queue = queue(4, 3, _ -> "ok");
        for (long id = 1; id <= 20; id++) {
            queue.submit(job(id));
        }
        queue.close();
        assertThat(threadsCreated.get()).isEqualTo(3);
        assertThat(queue.outcomes()).hasSize(20);
    }

    @Test
    void runsAtMostWorkersJobsConcurrently() throws InterruptedException {
        var current = new AtomicInteger();
        var peak = new AtomicInteger();
        var allWorkersBusy = new CountDownLatch(3);
        var queue = queue(20, 3, job -> {
            peak.accumulateAndGet(current.incrementAndGet(), Math::max);
            allWorkersBusy.countDown();
            try {
                Await.latch(gate);
            } finally {
                current.decrementAndGet();
            }
            return "ok";
        });
        for (long id = 1; id <= 10; id++) {
            queue.submit(job(id));
        }
        Await.latch(allWorkersBusy);                    // 3 handlers are running and blocked at the gate
        assertThat(peak.get()).isEqualTo(3);
        gate.countDown();
        queue.close();
        assertThat(peak.get()).isEqualTo(3);
        assertThat(queue.outcomes()).hasSize(10);
    }

    @Test
    void trySubmitBalksWhenQueueIsFull() throws InterruptedException {
        var queue = queue(1, 1, gated);
        queue.submit(job(1));
        Await.latch(entered);                           // job 1 runs (blocked), the queue is empty again
        assertThat(queue.trySubmit(job(2))).isTrue();   // fills the single slot
        assertThat(queue.trySubmit(job(3))).isFalse();  // full: returns at once
    }

    @Test
    void submitBlocksWhileQueueIsFullAndResumesWhenSpaceFrees() throws Exception {
        var queue = queue(1, 1, gated);
        queue.submit(job(1));
        Await.latch(entered);
        queue.submit(job(2));
        var submitted = new CompletableFuture<Void>();
        Thread submitter = Thread.ofVirtual().start(() -> {
            try {
                queue.submit(job(3));
                submitted.complete(null);
            } catch (InterruptedException | RuntimeException e) {
                submitted.completeExceptionally(e);
            }
        });
        Await.untilBlocked(submitter);
        assertThat(submitted).isNotDone();

        gate.countDown();                               // jobs 1 and 2 finish, job 3 gets in
        submitted.get(5, TimeUnit.SECONDS);
        queue.close();
        assertThat(queue.outcomes()).extracting(JobOutcome::jobId).containsExactly(1L, 2L, 3L);
    }

    @Test
    void shutdownDrainsQueuedJobsBeforeTermination() throws InterruptedException {
        var queue = queue(5, 1, gated);
        for (long id = 1; id <= 5; id++) {
            queue.submit(job(id));
        }
        Await.latch(entered);
        queue.shutdown();
        gate.countDown();
        assertThat(queue.awaitTermination(Await.BOUND)).isTrue();
        assertThat(queue.outcomes()).extracting(JobOutcome::jobId).containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    void shutdownDoesNotBlockWhenQueueIsFull() throws InterruptedException {
        var queue = queue(2, 1, gated);
        queue.submit(job(1));
        Await.latch(entered);
        queue.submit(job(2));
        queue.submit(job(3));                           // full now, and the worker is stuck at the gate
        queue.shutdown();                               // must return at once (the class timeout guards this)
        assertThat(queue.trySubmit(job(4))).isFalse();
        gate.countDown();
        assertThat(queue.awaitTermination(Await.BOUND)).isTrue();
        assertThat(queue.outcomes()).hasSize(3);
    }

    @Test
    void blockedSubmitterIsReleasedByShutdown() throws Exception {
        var queue = queue(1, 1, gated);
        queue.submit(job(1));
        Await.latch(entered);
        queue.submit(job(2));
        var submitted = new CompletableFuture<Void>();
        Thread submitter = Thread.ofVirtual().start(() -> {
            try {
                queue.submit(job(3));
                submitted.complete(null);
            } catch (InterruptedException | RuntimeException e) {
                submitted.completeExceptionally(e);
            }
        });
        Await.untilBlocked(submitter);

        queue.shutdown();
        assertThatThrownBy(() -> submitted.get(5, TimeUnit.SECONDS))
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    void submitAfterShutdownIsRejected() {
        var queue = queue(2, 1, _ -> "ok");
        queue.shutdown();
        assertThatThrownBy(() -> queue.submit(job(1))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void trySubmitAfterShutdownReturnsFalse() {
        var queue = queue(2, 1, _ -> "ok");
        queue.shutdown();
        assertThat(queue.trySubmit(job(1))).isFalse();
    }

    @Test
    void shutdownNowReturnsJobsThatNeverStartedInSubmissionOrder() throws InterruptedException {
        var queue = queue(5, 1, gated);
        queue.submit(job(1));
        Await.latch(entered);
        queue.submit(job(4));
        queue.submit(job(2));
        queue.submit(job(3));
        assertThat(queue.shutdownNow()).containsExactly(job(4), job(2), job(3));
        assertThat(queue.awaitTermination(Await.BOUND)).isTrue();
        assertThat(queue.outcomes()).extracting(JobOutcome::jobId).containsExactly(1L);
    }

    @Test
    void shutdownNowInterruptsRunningJobs() throws InterruptedException {
        var interrupted = new CountDownLatch(1);
        var queue = queue(2, 1, job -> {
            entered.countDown();
            try {
                new CountDownLatch(1).await();          // only an interrupt ends this job
                return "never";
            } catch (InterruptedException e) {
                interrupted.countDown();
                throw e;
            }
        });
        queue.submit(job(1));
        Await.latch(entered);
        queue.shutdownNow();
        Await.latch(interrupted);
        assertThat(queue.awaitTermination(Await.BOUND)).isTrue();
        assertThat(queue.outcomes()).singleElement().isInstanceOf(JobOutcome.Failed.class);
    }

    @Test
    void awaitTerminationReturnsFalseWhileJobsStillRun() throws InterruptedException {
        var queue = queue(2, 1, gated);
        queue.submit(job(1));
        Await.latch(entered);
        queue.shutdown();
        assertThat(queue.awaitTermination(Duration.ofMillis(50))).isFalse();
        gate.countDown();
        assertThat(queue.awaitTermination(Await.BOUND)).isTrue();
    }

    @Test
    void shutdownIsIdempotent() throws InterruptedException {
        var queue = queue(4, 1, gated);
        queue.submit(job(1));
        Await.latch(entered);
        queue.submit(job(2));
        queue.shutdown();
        queue.shutdown();
        assertThat(queue.shutdownNow()).containsExactly(job(2));
        assertThat(queue.shutdownNow()).isEmpty();
        assertThat(queue.awaitTermination(Await.BOUND)).isTrue();
    }

    @Test
    void closeShutsDownAndAwaitsTermination() throws InterruptedException {
        var queue = queue(10, 2, job -> "ok " + job.id());
        LongStream.rangeClosed(1, 10).forEach(id -> assertThat(queue.trySubmit(job(id))).isTrue());
        queue.close();
        assertThat(queue.outcomes()).hasSize(10);       // close() returned only after every job ran
        assertThatThrownBy(() -> queue.submit(job(11))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rejectsInvalidConstructorArguments() {
        assertThatThrownBy(() -> newQueue(0, 1, _ -> "ok", countingFactory))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> newQueue(1, 0, _ -> "ok", countingFactory))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
