package io.github.aliturgutbozkurt.patterns.m10.examples.future;

import io.github.aliturgutbozkurt.patterns.m10.examples.future.quotes.CancellationFacts;
import io.github.aliturgutbozkurt.patterns.m10.examples.future.quotes.Quote;
import io.github.aliturgutbozkurt.patterns.m10.examples.future.quotes.QuoteFanOut;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/future/QuoteFanOutDemo.java}
 *
 * <p>The slow provider waits for a latch that the demo opens only at the very end, so every deadline fires.
 */
public final class QuoteFanOutDemo {

    private QuoteFanOutDemo() {}

    public static void main(String[] args) throws InterruptedException {
        var slowProviderMayAnswer = new CountDownLatch(1);
        Supplier<Quote> slowco = () -> {
            try {
                slowProviderMayAnswer.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return new Quote("slowco", 80_00);
        };
        List<Supplier<Quote>> providers = List.of(() -> new Quote("acme", 120_00),
                () -> new Quote("globex", 99_00), slowco);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var fanOut = new QuoteFanOut(executor);
            var quotes = fanOut.all(providers, Duration.ofMillis(50), new Quote("slowco", Long.MAX_VALUE)).join();
            System.out.println("completeOnTimeout(50 ms) per quote, input order: " + quotes);

            try {
                fanOut.allOrTimeout(providers, Duration.ofMillis(50)).join();
            } catch (CompletionException e) {
                System.out.println("orTimeout(50 ms) on the fan-out: CompletionException caused by "
                        + e.getCause().getClass().getSimpleName());
            }

            var failed = new CountDownLatch(1);
            Supplier<Quote> failing = () -> {
                failed.countDown();
                throw new IllegalStateException("provider down");
            };
            var strict = fanOut.allStrict(List.of(failing, slowco));
            failed.await();
            System.out.println("allOf after one provider failed: done = " + strict.isDone()
                    + " (still waiting for the slow sibling)");
            slowProviderMayAnswer.countDown();
            try {
                strict.get();
            } catch (ExecutionException e) {
                System.out.println("allOf once the sibling finished: completed exceptionally = "
                        + strict.isCompletedExceptionally());
            }

            var cf = CancellationFacts.cancelCompletableFuture(executor);
            System.out.println("CompletableFuture.cancel(true): cancelled = " + cf.cancelled()
                    + ", task interrupted = " + cf.interrupted());
            var plain = CancellationFacts.cancelExecutorFuture(executor);
            System.out.println("ExecutorService Future.cancel(true): cancelled = " + plain.cancelled()
                    + ", task interrupted = " + plain.interrupted());
        }
    }
}
