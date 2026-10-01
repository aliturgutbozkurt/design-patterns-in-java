package io.github.aliturgutbozkurt.patterns.m10.exercises.ex01;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;

/** Assignment 01 — your parallel price comparator ("structured concurrency by hand", final APIs only). */
public class ParallelPriceComparator implements PriceComparator {

    private final List<PriceProvider> providers;
    private final Duration deadline;
    private final FailurePolicy policy;
    private final ExecutorService executor;

    public ParallelPriceComparator(List<PriceProvider> providers, Duration deadline, FailurePolicy policy,
            ExecutorService executor) {
        // TODO(ex01): keep an immutable copy of the providers; reject null arguments.
        this.providers = providers;
        this.deadline = deadline;
        this.policy = policy;
        this.executor = executor;
    }

    @Override
    public Comparison compare(String sku) throws InterruptedException {
        // TODO(ex01): reject a null or blank sku (IllegalArgumentException).
        // TODO(ex01): call every provider concurrently on the injected executor (never shut it down).
        // TODO(ex01): one result per provider, in provider order; a provider still running at the deadline
        //             becomes TimedOut and must be cancelled (interrupted).
        // TODO(ex01): BEST_EFFORT -> an exception becomes Failed(provider, message);
        //             FAIL_FAST  -> cancel the others and throw ComparisonFailedException at once.
        throw new UnsupportedOperationException("TODO(ex01): implement compare(String) using " + providers.size()
                + " providers, " + deadline + ", " + policy + " and " + executor);
    }
}
