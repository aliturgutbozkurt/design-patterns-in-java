package io.github.aliturgutbozkurt.patterns.m10.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.Comparison;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.ComparisonFailedException;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.FailurePolicy;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.PriceComparator;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.PriceProvider;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.ProviderResult;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.Quote;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Reference solution for assignment 01: structured concurrency built by hand from final APIs. Every provider runs
 * in its own task on the injected executor; the deadline is measured from the start of {@link #compare}; whatever
 * is still running when the comparison is decided gets cancelled with an interrupt.
 *
 * @see "m10 lesson, section Structured Concurrency — ex01"
 */
public final class ParallelPriceComparator implements PriceComparator {

    /** What one provider task produced: its index, and either a quote or the exception it threw. */
    private record Attempt(int index, Quote quote, Exception failure) {}

    private final List<PriceProvider> providers;
    private final Duration deadline;
    private final FailurePolicy policy;
    private final ExecutorService executor;

    public ParallelPriceComparator(List<PriceProvider> providers, Duration deadline, FailurePolicy policy,
            ExecutorService executor) {
        this.providers = List.copyOf(providers);
        this.deadline = Objects.requireNonNull(deadline, "deadline");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.executor = Objects.requireNonNull(executor, "executor");
    }

    @Override
    public Comparison compare(String sku) throws InterruptedException {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        long deadlineNanos = System.nanoTime() + deadline.toNanos();
        return switch (policy) {
            case BEST_EFFORT -> bestEffort(sku);
            case FAIL_FAST -> failFast(sku, deadlineNanos);
        };
    }

    /** {@code invokeAll} with a timeout: returns futures in task order and cancels (interrupts) the late ones. */
    private Comparison bestEffort(String sku) throws InterruptedException {
        List<Callable<Attempt>> tasks = new ArrayList<>();
        for (int i = 0; i < providers.size(); i++) {
            int index = i;
            tasks.add(() -> attempt(index, sku));
        }
        List<Future<Attempt>> futures = executor.invokeAll(tasks, deadline.toNanos(), TimeUnit.NANOSECONDS);
        List<ProviderResult> results = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            Future<Attempt> future = futures.get(i);
            String name = providers.get(i).name();
            results.add(switch (future.state()) {
                case SUCCESS -> toResult(future.resultNow(), name);
                case CANCELLED -> new ProviderResult.TimedOut(name);
                case FAILED -> new ProviderResult.Failed(name, message(future.exceptionNow()));
                case RUNNING -> throw new IllegalStateException("invokeAll returned a running task");
            });
        }
        return new Comparison(sku, results);
    }

    /** A completion service hands out results as they finish, so the first failure is seen at once. */
    private Comparison failFast(String sku, long deadlineNanos) throws InterruptedException {
        var completion = new ExecutorCompletionService<Attempt>(executor);
        List<Future<Attempt>> futures = new ArrayList<>();
        ProviderResult[] results = new ProviderResult[providers.size()];
        try {
            for (int i = 0; i < providers.size(); i++) {
                int index = i;
                futures.add(completion.submit(() -> attempt(index, sku)));
            }
            for (int done = 0; done < providers.size(); done++) {
                Future<Attempt> next = completion.poll(deadlineNanos - System.nanoTime(), TimeUnit.NANOSECONDS);
                if (next == null) {
                    break;                                  // deadline: the rest become TimedOut
                }
                Attempt attempt = get(next);
                String name = providers.get(attempt.index()).name();
                if (attempt.failure() != null) {
                    throw new ComparisonFailedException(name, attempt.failure());   // finally cancels the rest
                }
                results[attempt.index()] = new ProviderResult.Priced(attempt.quote());
            }
        } finally {
            futures.forEach(future -> future.cancel(true));  // no-op for finished tasks
        }
        for (int i = 0; i < results.length; i++) {
            if (results[i] == null) {
                results[i] = new ProviderResult.TimedOut(providers.get(i).name());
            }
        }
        return new Comparison(sku, Arrays.asList(results));
    }

    /** Runs one provider; its exception is data here, the policy decides what it means. */
    private Attempt attempt(int index, String sku) throws InterruptedException {
        try {
            return new Attempt(index, providers.get(index).quote(sku), null);
        } catch (InterruptedException e) {
            throw e;                                        // cancelled: let the future record it
        } catch (Exception e) {
            return new Attempt(index, null, e);
        }
    }

    private static ProviderResult toResult(Attempt attempt, String name) {
        return attempt.failure() == null
                ? new ProviderResult.Priced(attempt.quote())
                : new ProviderResult.Failed(name, message(attempt.failure()));
    }

    private static Attempt get(Future<Attempt> done) throws InterruptedException {
        try {
            return done.get();
        } catch (ExecutionException e) {                    // only an InterruptedException escapes attempt()
            throw new IllegalStateException("provider task ended unexpectedly", e.getCause());
        }
    }

    private static String message(Throwable failure) {
        return failure.getMessage() != null ? failure.getMessage() : failure.getClass().getSimpleName();
    }
}
