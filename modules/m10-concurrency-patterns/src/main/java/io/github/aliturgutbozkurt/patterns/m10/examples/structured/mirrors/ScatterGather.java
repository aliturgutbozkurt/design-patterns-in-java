package io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

/**
 * Scatter-gather over search shards, with two policies. "All or nothing":
 * {@link Joiner#allSuccessfulOrThrow()} returns every result in <em>fork</em> order, or throws on the first failure
 * and cancels the rest. "Best effort": {@link BestEffortJoiner} keeps whatever succeeded. Preview API (JEP 533).
 *
 * @see "m10 lesson, section Structured Concurrency"
 */
public final class ScatterGather {

    private ScatterGather() {}

    public static <R> List<R> gatherAll(List<Callable<R>> shards) throws ExecutionException, InterruptedException {
        try (var scope = StructuredTaskScope.open(Joiner.<R>allSuccessfulOrThrow())) {
            shards.forEach(scope::fork);
            return scope.join();
        }
    }

    public static <R> List<R> gatherBestEffort(List<Callable<R>> shards, Duration timeout)
            throws InterruptedException {
        try (var scope = StructuredTaskScope.open(new BestEffortJoiner<R>(), config -> config.withTimeout(timeout))) {
            shards.forEach(scope::fork);
            return scope.join();                 // never throws for a failed shard or the timeout
        }
    }
}
