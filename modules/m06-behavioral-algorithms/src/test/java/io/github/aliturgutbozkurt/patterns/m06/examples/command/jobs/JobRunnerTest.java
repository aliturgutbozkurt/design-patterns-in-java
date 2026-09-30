package io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.Job.GenerateReport;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.Job.ResizeImage;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.Job.SendEmail;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.JobResult.Status;
import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class JobRunnerTest {

    @Test
    void jobsRunInFifoOrderWhenDrainedSequentially() {
        var order = new ArrayList<Job>();
        var runner = new JobRunner(job -> {
            order.add(job);
            return "ok";
        }, 1);
        var queue = new JobQueue();
        List<Job> jobs = List.of(new GenerateReport("a"), new SendEmail("b@x", "hi"), new ResizeImage("c", 1));
        jobs.forEach(queue::submit);
        runner.runSequentially(queue);
        assertThat(order).isEqualTo(jobs);
        assertThat(queue.size()).isZero();
    }

    @Test
    void resultsOfConcurrentJobsComeBackInSubmissionOrder() throws InterruptedException {
        var runner = new JobRunner(job -> {
            if (job.equals(new GenerateReport("r0"))) {
                sleepMillis(50);  // the first job finishes last
            }
            return JobRunner.perform(job);
        }, 1);
        var queue = new JobQueue();
        var submitted = new ArrayList<Job>();
        for (int i = 0; i < 100; i++) {
            submitted.add(new GenerateReport("r" + i));
            queue.submit(submitted.getLast());
        }
        List<JobResult> results = runner.runConcurrently(queue);
        assertThat(results).extracting(JobResult::job).isEqualTo(submitted);
        assertThat(results).allMatch(r -> r.status() == Status.SUCCEEDED);
    }

    @Test
    void aFailingJobBecomesAFailedResultInsteadOfStoppingTheRun() throws InterruptedException {
        var queue = new JobQueue();
        queue.submit(new ResizeImage("bad.png", 0));
        queue.submit(new GenerateReport("after"));
        List<JobResult> results = new JobRunner(JobRunner::perform, 1).runConcurrently(queue);
        assertThat(results.get(0)).isEqualTo(
                new JobResult(new ResizeImage("bad.png", 0), Status.FAILED, "width must be > 0: 0", 1));
        assertThat(results.get(1).status()).isEqualTo(Status.SUCCEEDED);
    }

    @Test
    void aFailedJobIsRetriedUpToTheLimit() {
        var calls = new AtomicInteger();
        var flaky = new JobRunner(_ -> {
            if (calls.incrementAndGet() < 3) {
                throw new IllegalStateException("temporarily unavailable");
            }
            return "done";
        }, 3);
        JobResult result = flaky.run(new GenerateReport("flaky"));
        assertThat(result.status()).isEqualTo(Status.SUCCEEDED);
        assertThat(result.attempts()).isEqualTo(3);

        calls.set(0);
        var impatient = new JobRunner(_ -> {
            calls.incrementAndGet();
            throw new IllegalStateException("down");
        }, 2);
        assertThat(impatient.run(new GenerateReport("x")))
                .isEqualTo(new JobResult(new GenerateReport("x"), Status.FAILED, "down", 2));
        assertThat(calls).hasValue(2);
    }

    @Test
    void demoPrintsEachResultAndTheConcurrentSummary() {
        assertThat(Console.capture(() -> {
            try {
                JobQueueDemo.main(new String[0]);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError(e);
            }
        })).isEqualTo("""
                queued 4 jobs
                SUCCEEDED  SendEmail[to=ada@example.com, subject=Welcome] -> sent 'Welcome' to ada@example.com (attempts: 1)
                SUCCEEDED  ResizeImage[file=logo.png, width=128] -> resized logo.png to 128px (attempts: 1)
                FAILED     ResizeImage[file=banner.png, width=0] -> width must be > 0: 0 (attempts: 3)
                SUCCEEDED  GenerateReport[name=weekly] -> report weekly ready (attempts: 1)
                100 report jobs on virtual threads: 100 succeeded, results in submission order: true
                """);
    }

    private static void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
