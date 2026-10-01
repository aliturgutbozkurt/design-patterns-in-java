package io.github.aliturgutbozkurt.patterns.m10.exercises.ex02;

/** GIVEN — do not modify. Does the work of one job; may block, may throw, should stop when interrupted. */
@FunctionalInterface
public interface JobHandler {

    String handle(Job job) throws Exception;
}
