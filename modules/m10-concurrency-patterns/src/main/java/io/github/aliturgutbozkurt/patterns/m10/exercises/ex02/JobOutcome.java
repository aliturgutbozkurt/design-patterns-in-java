package io.github.aliturgutbozkurt.patterns.m10.exercises.ex02;

/** GIVEN — do not modify. What happened to a job that a worker ran. */
public sealed interface JobOutcome {

    long jobId();

    /** The handler returned {@code result}. */
    record Completed(long jobId, String result) implements JobOutcome {}

    /** The handler threw; {@code error} is the exception's message. */
    record Failed(long jobId, String error) implements JobOutcome {}
}
