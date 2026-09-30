package io.github.aliturgutbozkurt.patterns.m10.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.support.Await;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Assignment 01 — parallel price comparison with a deadline and a failure policy. Each test is one acceptance
 * criterion of the brief. No test sleeps: order is forced with latches, and a provider that "never answers" waits
 * for a latch that is only opened when the test is over.
 */
@Timeout(10)
public abstract class Ex01Contract {

    protected abstract PriceComparator comparator(List<PriceProvider> providers, Duration deadline,
            FailurePolicy policy, ExecutorService executor);

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final CountDownLatch endOfTest = new CountDownLatch(1);

    @AfterEach
    void releaseEverythingAndStopTheExecutor() throws InterruptedException {
        endOfTest.countDown();                          // frees providers that "never" answer
        executor.shutdownNow();
        assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).as("executor terminated").isTrue();
    }

    private static PriceProvider provider(String name, Callable<Quote> call) {
        return new PriceProvider() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public Quote quote(String sku) throws Exception {
                return call.call();
            }
        };
    }

    private static PriceProvider priced(String name, long cents) {
        return provider(name, () -> new Quote(name, cents));
    }

    /** Signals {@code started}, then waits for the end of the test; an interrupt counts down {@code interrupted}. */
    private PriceProvider hanging(String name, CountDownLatch started, CountDownLatch interrupted) {
        return provider(name, () -> {
            started.countDown();
            try {
                endOfTest.await();
            } catch (InterruptedException e) {
                interrupted.countDown();
                throw e;
            }
            return new Quote(name, 1);
        });
    }

    private PriceComparator comparator(List<PriceProvider> providers, Duration deadline, FailurePolicy policy) {
        return comparator(providers, deadline, policy, executor);
    }

    private Comparison bestEffort(PriceProvider... providers) throws InterruptedException {
        return comparator(List.of(providers), Await.BOUND, FailurePolicy.BEST_EFFORT).compare("SKU-1");
    }

    @Test
    void returnsOneResultPerProviderInProviderOrder() throws InterruptedException {
        var lastDone = new CountDownLatch(1);
        var first = provider("first", () -> {
            Await.latch(lastDone);                      // the first provider answers last
            return new Quote("first", 30_00);
        });
        var last = provider("last", () -> {
            lastDone.countDown();
            return new Quote("last", 10_00);
        });
        var comparison = bestEffort(first, priced("middle", 20_00), last);
        assertThat(comparison.sku()).isEqualTo("SKU-1");
        assertThat(comparison.results()).containsExactly(
                new ProviderResult.Priced(new Quote("first", 30_00)),
                new ProviderResult.Priced(new Quote("middle", 20_00)),
                new ProviderResult.Priced(new Quote("last", 10_00)));
    }

    @Test
    void providersAreCalledConcurrently() throws InterruptedException {
        int count = 4;
        var allInFlight = new CountDownLatch(count);
        List<PriceProvider> providers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = "p" + i;
            providers.add(provider(name, () -> {
                allInFlight.countDown();                // opens only if all providers run at the same time
                if (!allInFlight.await(4, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("providers were not called concurrently");
                }
                return new Quote(name, 100);
            }));
        }
        var comparison = comparator(providers, Duration.ofSeconds(5), FailurePolicy.BEST_EFFORT).compare("SKU-1");
        assertThat(comparison.results()).hasSize(count).allMatch(ProviderResult.Priced.class::isInstance);
    }

    @Test
    void cheapestPicksLowestPricedQuote() throws InterruptedException {
        var comparison = bestEffort(priced("a", 30_00), priced("b", 10_00), priced("c", 20_00));
        assertThat(comparison.cheapest()).contains(new Quote("b", 10_00));
    }

    @Test
    void cheapestTieGoesToEarlierProvider() throws InterruptedException {
        var comparison = bestEffort(priced("a", 30_00), priced("b", 10_00), priced("c", 10_00));
        assertThat(comparison.cheapest()).contains(new Quote("b", 10_00));
    }

    @Test
    void cheapestIsEmptyWhenNoQuoteSucceeded() throws InterruptedException {
        var comparison = bestEffort(provider("a", () -> {
            throw new IOException("offline");
        }));
        assertThat(comparison.cheapest()).isEmpty();
    }

    @Test
    void bestEffortReportsFailedProviderAndKeepsOthers() throws InterruptedException {
        var comparison = bestEffort(priced("a", 10_00), provider("b", () -> {
            throw new IOException("offline");
        }), priced("c", 20_00));
        assertThat(comparison.results()).containsExactly(
                new ProviderResult.Priced(new Quote("a", 10_00)),
                new ProviderResult.Failed("b", "offline"),
                new ProviderResult.Priced(new Quote("c", 20_00)));
    }

    @Test
    void slowProviderIsReportedAsTimedOut() throws InterruptedException {
        var comparison = comparator(List.of(priced("a", 10_00), hanging("slow", new CountDownLatch(1),
                new CountDownLatch(1))), Duration.ofMillis(100), FailurePolicy.BEST_EFFORT).compare("SKU-1");
        assertThat(comparison.results()).containsExactly(
                new ProviderResult.Priced(new Quote("a", 10_00)), new ProviderResult.TimedOut("slow"));
    }

    @Test
    void slowProviderIsInterruptedAfterDeadline() throws InterruptedException {
        var started = new CountDownLatch(1);
        var interrupted = new CountDownLatch(1);
        comparator(List.of(hanging("slow", started, interrupted)), Duration.ofMillis(100), FailurePolicy.BEST_EFFORT)
                .compare("SKU-1");
        if (started.getCount() == 0) {                  // a task cancelled before it started cannot be interrupted
            Await.latch(interrupted);
        }
    }

    @Test
    void failFastThrowsNamingTheFailingProvider() {
        var offline = new IOException("offline");
        var comparator = comparator(List.of(priced("a", 10_00), provider("b", () -> {
            throw offline;
        })), Await.BOUND, FailurePolicy.FAIL_FAST);
        assertThatThrownBy(() -> comparator.compare("SKU-1"))
                .isInstanceOfSatisfying(ComparisonFailedException.class, e -> assertThat(e.provider()).isEqualTo("b"))
                .hasCause(offline);
    }

    @Test
    void failFastInterruptsRemainingProviders() throws InterruptedException {
        var started = new CountDownLatch(1);
        var interrupted = new CountDownLatch(1);
        var failing = provider("failing", () -> {
            Await.latch(started);                       // fail only once the other provider is running
            throw new IOException("offline");
        });
        var comparator = comparator(List.of(hanging("slow", started, interrupted), failing), Await.BOUND,
                FailurePolicy.FAIL_FAST);
        assertThatThrownBy(() -> comparator.compare("SKU-1")).isInstanceOf(ComparisonFailedException.class);
        Await.latch(interrupted);
    }

    @Test
    @Timeout(5)
    void failFastDoesNotWaitForTheDeadline() {
        var started = new CountDownLatch(1);
        var failing = provider("failing", () -> {
            Await.latch(started);
            throw new IOException("offline");
        });
        var comparator = comparator(List.of(hanging("slow", started, new CountDownLatch(1)), failing),
                Duration.ofSeconds(30), FailurePolicy.FAIL_FAST);
        assertThatThrownBy(() -> comparator.compare("SKU-1")).isInstanceOf(ComparisonFailedException.class);
    }

    @Test
    void emptyProviderListGivesEmptyComparison() throws InterruptedException {
        var comparison = bestEffort();
        assertThat(comparison.results()).isEmpty();
        assertThat(comparison.cheapest()).isEmpty();
    }

    @Test
    void resultsListIsImmutable() throws InterruptedException {
        var comparison = bestEffort(priced("a", 10_00));
        assertThatThrownBy(() -> comparison.results().add(new ProviderResult.TimedOut("x")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void doesNotShutDownTheInjectedExecutor() throws InterruptedException {
        bestEffort(priced("a", 10_00));
        assertThatThrownBy(() -> comparator(List.of(provider("b", () -> {
            throw new IOException("offline");
        })), Await.BOUND, FailurePolicy.FAIL_FAST).compare("SKU-1")).isInstanceOf(ComparisonFailedException.class);
        assertThat(executor.isShutdown()).isFalse();
    }

    @Test
    void rejectsNullOrBlankSku() {
        var comparator = comparator(List.of(priced("a", 10_00)), Await.BOUND, FailurePolicy.BEST_EFFORT);
        assertThatThrownBy(() -> comparator.compare(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> comparator.compare("  ")).isInstanceOf(IllegalArgumentException.class);
    }
}
