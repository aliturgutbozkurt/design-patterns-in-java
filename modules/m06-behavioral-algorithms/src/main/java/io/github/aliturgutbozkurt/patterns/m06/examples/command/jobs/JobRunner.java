package io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs;

import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.Job.GenerateReport;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.Job.ResizeImage;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.Job.SendEmail;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.JobResult.Status;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * The invoker for queued jobs. Each job becomes a {@link Callable}, the JDK's own Command type, and an
 * {@code ExecutorService} running virtual threads plays the invoker.
 *
 * @see "m06 lesson, section Command"
 */
public final class JobRunner {

    private final Function<Job, String> worker;
    private final int maxAttempts;

    /** @param worker performs one job and returns its output; it may throw to signal a failure */
    public JobRunner(Function<Job, String> worker, int maxAttempts) {
        this.worker = Objects.requireNonNull(worker, "worker");
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1: " + maxAttempts);
        }
        this.maxAttempts = maxAttempts;
    }

    /** The default worker: one exhaustive {@code switch} over the job types (no {@code default}). */
    public static String perform(Job job) {
        return switch (job) {
            case SendEmail(String to, String subject) -> "sent '" + subject + "' to " + to;
            case ResizeImage(String file, int width) -> {
                if (width <= 0) {
                    throw new IllegalArgumentException("width must be > 0: " + width);
                }
                yield "resized " + file + " to " + width + "px";
            }
            case GenerateReport(String name) -> "report " + name + " ready";
        };
    }

    /** Runs one job, retrying up to {@code maxAttempts}; a failure becomes a {@code FAILED} result, never an exception. */
    public JobResult run(Job job) {
        RuntimeException lastError = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return new JobResult(job, Status.SUCCEEDED, worker.apply(job), attempt);
            } catch (RuntimeException e) {
                lastError = e;
            }
        }
        return new JobResult(job, Status.FAILED, String.valueOf(lastError.getMessage()), maxAttempts);
    }

    /** Drains the queue and runs the jobs one after another, in FIFO order. */
    public List<JobResult> runSequentially(JobQueue queue) {
        return queue.drain().stream().map(this::run).toList();
    }

    /**
     * Drains the queue and runs every job on its own virtual thread. {@code invokeAll} returns the futures in task
     * order, so the results are in submission order even though the jobs finish in any order.
     */
    public List<JobResult> runConcurrently(JobQueue queue) throws InterruptedException {
        List<Callable<JobResult>> tasks = queue.drain().stream()
                .<Callable<JobResult>>map(job -> () -> run(job))
                .toList();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            return executor.invokeAll(tasks).stream().map(Future::resultNow).toList();
        }
    }
}
