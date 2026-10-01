package io.github.aliturgutbozkurt.patterns.m10.examples.future.quotes;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Fan-out/fan-in with {@link CompletableFuture#allOf}: ask every provider at once, then read the results in
 * <em>input</em> order (not completion order). Deadlines come from {@code completeOnTimeout} (substitute a value)
 * or {@code orTimeout} (fail). Note what {@code allOf} does not do: it never gives up early when one provider fails,
 * and it never cancels the others.
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
public final class QuoteFanOut {

    private final Executor executor;

    public QuoteFanOut(Executor executor) {
        this.executor = Objects.requireNonNull(executor, "executor");
    }

    /** Every provider gets {@code perQuoteDeadline}; one that misses it counts as {@code fallback}. */
    public CompletableFuture<List<Quote>> all(List<Supplier<Quote>> providers, Duration perQuoteDeadline,
            Quote fallback) {
        List<CompletableFuture<Quote>> quotes = providers.stream()
                .map(provider -> CompletableFuture.supplyAsync(provider, executor)
                        .completeOnTimeout(fallback, perQuoteDeadline.toNanos(), TimeUnit.NANOSECONDS))
                .toList();
        return inInputOrder(quotes);
    }

    /** The whole fan-out must finish within {@code deadline}, or it fails with a {@code TimeoutException}. */
    public CompletableFuture<List<Quote>> allOrTimeout(List<Supplier<Quote>> providers, Duration deadline) {
        return allStrict(providers).orTimeout(deadline.toNanos(), TimeUnit.NANOSECONDS);
    }

    /** No deadline: completes when every provider has finished, exceptionally if any of them failed. */
    public CompletableFuture<List<Quote>> allStrict(List<Supplier<Quote>> providers) {
        return inInputOrder(providers.stream()
                .map(provider -> CompletableFuture.supplyAsync(provider, executor))
                .toList());
    }

    private static CompletableFuture<List<Quote>> inInputOrder(List<CompletableFuture<Quote>> quotes) {
        return CompletableFuture.allOf(quotes.toArray(CompletableFuture[]::new))
                .thenApply(_ -> quotes.stream().map(CompletableFuture::join).toList());   // all done: join is safe
    }
}
