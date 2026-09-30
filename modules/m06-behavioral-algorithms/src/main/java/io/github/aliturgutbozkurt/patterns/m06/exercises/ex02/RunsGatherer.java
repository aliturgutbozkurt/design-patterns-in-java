package io.github.aliturgutbozkurt.patterns.m06.exercises.ex02;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Gatherer;

/** Assignment 02 — your custom gatherer that groups consecutive elements into runs. */
public final class RunsGatherer {

    private RunsGatherer() {}

    public static <T> Gatherer<T, ?, List<T>> runs(BiPredicate<? super T, ? super T> sameRun) {
        // TODO(ex02): Gatherer.ofSequential(initializer, integrator, finisher); return downstream.push(...)'s result.
        throw new UnsupportedOperationException("TODO(ex02): implement runs(BiPredicate)");
    }
}
