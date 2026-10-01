package io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs;

import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.Job.GenerateReport;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.Job.ResizeImage;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.Job.SendEmail;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs.JobResult.Status;
import java.util.ArrayList;
import java.util.List;

/**
 * Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/command/jobs/JobQueueDemo.java}
 *
 * @see "m06 lesson, section Command"
 */
public final class JobQueueDemo {

    private JobQueueDemo() {}

    public static void main(String[] args) throws InterruptedException {
        var runner = new JobRunner(JobRunner::perform, 3);

        var queue = new JobQueue();
        queue.submit(new SendEmail("ada@example.com", "Welcome"));
        queue.submit(new ResizeImage("logo.png", 128));
        queue.submit(new ResizeImage("banner.png", 0));  // fails on every attempt
        queue.submit(new GenerateReport("weekly"));
        System.out.println("queued " + queue.size() + " jobs");
        runner.runSequentially(queue).forEach(result -> System.out.println(result.describe()));

        List<Job> reports = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            reports.add(new GenerateReport("r" + i));
            queue.submit(reports.getLast());
        }
        List<JobResult> results = runner.runConcurrently(queue);
        long succeeded = results.stream().filter(r -> r.status() == Status.SUCCEEDED).count();
        boolean inOrder = results.stream().map(JobResult::job).toList().equals(reports);
        System.out.println("100 report jobs on virtual threads: " + succeeded
                + " succeeded, results in submission order: " + inOrder);
    }
}
