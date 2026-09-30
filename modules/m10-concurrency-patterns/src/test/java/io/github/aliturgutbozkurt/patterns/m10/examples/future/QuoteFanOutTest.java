package io.github.aliturgutbozkurt.patterns.m10.examples.future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.future.quotes.CancellationFacts;
import io.github.aliturgutbozkurt.patterns.m10.examples.future.quotes.Quote;
import io.github.aliturgutbozkurt.patterns.m10.examples.future.quotes.QuoteFanOut;
import io.github.aliturgutbozkurt.patterns.m10.support.Await;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class QuoteFanOutTest {

    private static final Quote FALLBACK = new Quote("fallback", Long.MAX_VALUE);

    /** A provider that blocks until {@code release} opens (tests open it in {@code finally}). */
    private static Supplier<Quote> blockedUntil(CountDownLatch release, Quote quote) {
        return () -> {
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            return quote;
        };
    }

    @Test
    void resultsComeInInputOrderEvenWhenTheLaterProviderFinishesFirst() throws Exception {
        var secondDone = new CountDownLatch(1);
        var first = new Quote("acme", 120_00);
        var second = new Quote("globex", 99_00);
        Supplier<Quote> slowFirst = () -> {
            try {
                Await.latch(secondDone);                  // finishes only after the second one
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            return first;
        };
        Supplier<Quote> fastSecond = () -> {
            secondDone.countDown();
            return second;
        };
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var quotes = new QuoteFanOut(executor).all(List.of(slowFirst, fastSecond), Await.BOUND, FALLBACK);
            assertThat(quotes.get(5, TimeUnit.SECONDS)).containsExactly(first, second);
        }
    }

    @Test
    void aProviderThatNeverAnswersIsReplacedByTheFallbackAfterTheDeadline() throws Exception {
        var never = new CountDownLatch(1);
        var fast = new Quote("acme", 120_00);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            try {
                var quotes = new QuoteFanOut(executor)
                        .all(List.of(() -> fast, blockedUntil(never, new Quote("slowco", 1))), Duration.ofMillis(50),
                                FALLBACK);
                assertThat(quotes.get(5, TimeUnit.SECONDS)).containsExactly(fast, FALLBACK);
            } finally {
                never.countDown();                        // let the abandoned provider finish before close()
            }
        }
    }

    @Test
    void orTimeoutFailsTheWholeFanOutWithTimeoutException() {
        var never = new CountDownLatch(1);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            try {
                var quotes = new QuoteFanOut(executor)
                        .allOrTimeout(List.of(blockedUntil(never, new Quote("slowco", 1))), Duration.ofMillis(50));
                assertThatThrownBy(quotes::join).isInstanceOf(CompletionException.class)
                        .hasCauseInstanceOf(TimeoutException.class);
            } finally {
                never.countDown();
            }
        }
    }

    @Test
    void allOfKeepsWaitingForAPendingSiblingAfterAnotherOneFailed() throws Exception {
        var release = new CountDownLatch(1);
        var failed = new CountDownLatch(1);
        Supplier<Quote> failing = () -> {
            failed.countDown();
            throw new IllegalStateException("provider down");
        };
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var quotes = new QuoteFanOut(executor).allStrict(
                    List.of(failing, blockedUntil(release, new Quote("slowco", 1))));
            Await.latch(failed);
            assertThat(quotes).isNotDone();               // one failed, but allOf does not give up early
            release.countDown();
            assertThatThrownBy(() -> quotes.get(5, TimeUnit.SECONDS))
                    .hasCauseInstanceOf(IllegalStateException.class);
            assertThat(quotes).isCompletedExceptionally();
        }
    }

    @Test
    void cancelTrueOnACompletableFutureDoesNotInterruptTheRunningTask() throws InterruptedException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var outcome = CancellationFacts.cancelCompletableFuture(executor);
            assertThat(outcome.cancelled()).isTrue();
            assertThat(outcome.interrupted()).isFalse();  // the task ran to its end
        }
    }

    @Test
    void cancelTrueOnAnExecutorServiceFutureDoesInterruptTheTask() throws InterruptedException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var outcome = CancellationFacts.cancelExecutorFuture(executor);
            assertThat(outcome.cancelled()).isTrue();
            assertThat(outcome.interrupted()).isTrue();
        }
    }

    @Test
    void demoPrintsFanOutResultsAndTheLimits() {
        assertThat(Demos.output(() -> QuoteFanOutDemo.main(new String[0]))).isEqualTo("""
                completeOnTimeout(50 ms) per quote, input order: [acme 120.00, globex 99.00, slowco FALLBACK]
                orTimeout(50 ms) on the fan-out: CompletionException caused by TimeoutException
                allOf after one provider failed: done = false (still waiting for the slow sibling)
                allOf once the sibling finished: completed exceptionally = true
                CompletableFuture.cancel(true): cancelled = true, task interrupted = false
                ExecutorService Future.cancel(true): cancelled = true, task interrupted = true
                """);
    }
}
