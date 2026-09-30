package io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs;

import java.util.Locale;
import java.util.Objects;

/**
 * What happened to one job: its status, a message (the output or the error) and how many attempts it took.
 *
 * @see "m06 lesson, section Command"
 */
public record JobResult(Job job, Status status, String message, int attempts) {

    /** Outcome of a job after all its attempts. */
    public enum Status { SUCCEEDED, FAILED }

    public JobResult {
        Objects.requireNonNull(job, "job");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(message, "message");
    }

    /** E.g. {@code "FAILED     ResizeImage[file=a.png, width=0] -> width must be > 0: 0 (attempts: 3)"}. */
    public String describe() {
        return String.format(Locale.ROOT, "%-10s %s -> %s (attempts: %d)", status, job, message, attempts);
    }
}
