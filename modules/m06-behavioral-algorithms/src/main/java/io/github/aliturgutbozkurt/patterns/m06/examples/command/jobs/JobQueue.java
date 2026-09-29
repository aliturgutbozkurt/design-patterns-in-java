package io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * A first-in, first-out queue of jobs. Not thread-safe: one thread fills it, then a {@link JobRunner} drains it.
 *
 * @see "m06 lesson, section Command"
 */
public final class JobQueue {

    private final Deque<Job> jobs = new ArrayDeque<>();

    public void submit(Job job) {
        jobs.addLast(Objects.requireNonNull(job, "job"));
    }

    public int size() {
        return jobs.size();
    }

    /** Removes and returns every queued job, oldest first. */
    public List<Job> drain() {
        List<Job> all = new ArrayList<>(jobs);
        jobs.clear();
        return all;
    }
}
