package io.github.aliturgutbozkurt.patterns.m10.examples.future.quotes;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * What {@code cancel(true)} really does. On a {@link CompletableFuture} it only completes the future with a
 * {@code CancellationException}: the task keeps running, because the future does not know which thread runs it.
 * On a {@link Future} from {@link ExecutorService#submit} it interrupts the task's thread. Stopping the work that
 * is no longer needed is exactly what structured concurrency adds.
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
public final class CancellationFacts {

    /**
     * What the cancelled future reported and what the running task observed.
     *
     * @see "m10 lesson, section CompletableFuture pipelines"
     */
    public record Outcome(boolean cancelled, boolean interrupted) {}

    private CancellationFacts() {}

    /** Cancels a running {@code supplyAsync} task, then lets it finish and reports whether it was interrupted. */
    public static Outcome cancelCompletableFuture(Executor executor) throws InterruptedException {
        var started = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var finished = new CountDownLatch(1);
        var interrupted = new AtomicBoolean();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            started.countDown();
            awaitRecordingInterrupt(release, interrupted);
            finished.countDown();
            return "done";
        }, executor);
        await(started);
        future.cancel(true);
        release.countDown();                            // an uninterrupted task gets here only through the latch
        await(finished);
        return new Outcome(future.isCancelled(), interrupted.get());
    }

    /** Cancels a running {@code ExecutorService} task and reports whether it was interrupted. */
    public static Outcome cancelExecutorFuture(ExecutorService executor) throws InterruptedException {
        var started = new CountDownLatch(1);
        var finished = new CountDownLatch(1);
        var interrupted = new AtomicBoolean();
        Future<?> future = executor.submit(() -> {
            started.countDown();
            awaitRecordingInterrupt(new CountDownLatch(1), interrupted);   // never opened: only an interrupt ends it
            finished.countDown();
        });
        await(started);
        future.cancel(true);
        await(finished);
        return new Outcome(future.isCancelled(), interrupted.get());
    }

    private static void awaitRecordingInterrupt(CountDownLatch latch, AtomicBoolean interrupted) {
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("latch not opened within 5 s");
            }
        } catch (InterruptedException e) {
            interrupted.set(true);
            Thread.currentThread().interrupt();
        }
    }

    private static void await(CountDownLatch latch) throws InterruptedException {
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("task did not reach the expected point within 5 s");
        }
    }
}
