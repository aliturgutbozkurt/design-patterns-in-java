package io.github.aliturgutbozkurt.patterns.m10.examples.structured;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors.BestEffortJoiner;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors.Download;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors.Mirror;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors.MirrorDownloader;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors.ScatterGather;
import io.github.aliturgutbozkurt.patterns.m10.support.Await;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** Preview API (JEP 533): compiled and run with {@code --enable-preview} by this module's POM. */
@Timeout(10)
class MirrorDownloadTest {

    /** Signals {@code started}, blocks until interrupted (the latch is never opened) and records the interrupt. */
    private static String hang(CountDownLatch started, CountDownLatch interrupted) throws InterruptedException {
        started.countDown();
        try {
            new CountDownLatch(1).await();
            throw new AssertionError("unreachable");
        } catch (InterruptedException e) {
            interrupted.countDown();
            throw e;
        }
    }

    private static void awaitSuccess(Subtask<?> subtask) {
        long deadline = System.nanoTime() + Await.BOUND.toNanos();
        while (subtask.state() != Subtask.State.SUCCESS) {
            if (System.nanoTime() - deadline > 0) {
                throw new AssertionError("subtask did not succeed within " + Await.BOUND);
            }
            Thread.onSpinWait();
        }
    }

    @Test
    void firstSuccessfulMirrorWinsAndTheBlockedMirrorIsInterrupted() throws Exception {
        var slowStarted = new CountDownLatch(1);
        var slowInterrupted = new CountDownLatch(1);
        var downloader = new MirrorDownloader(List.of(
                new Mirror("eu-slow", _ -> hang(slowStarted, slowInterrupted)),
                new Mirror("us-fast", file -> {
                    Await.latch(slowStarted);           // win only once the slow mirror is really running
                    return file + " from us-fast";
                })));
        assertThat(downloader.download("report.pdf")).isEqualTo(new Download("us-fast", "report.pdf from us-fast"));
        assertThat(slowInterrupted.getCount()).as("slow mirror interrupted before download() returned").isZero();
    }

    @Test
    void whenEveryMirrorFailsDownloadThrowsExecutionException() {
        var downloader = new MirrorDownloader(List.of(
                new Mirror("a", _ -> {
                    throw new IOException("a: connection refused");
                }),
                new Mirror("b", _ -> {
                    throw new IOException("b: connection refused");
                })));
        assertThatThrownBy(() -> downloader.download("report.pdf")).isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void anySuccessfulOrThrowWithoutForksFailsWithNoSuchElementException() {
        assertThatThrownBy(() -> new MirrorDownloader(List.of()).download("report.pdf"))
                .isInstanceOf(ExecutionException.class).hasCauseInstanceOf(NoSuchElementException.class);
    }

    @Test
    void allSuccessfulOrThrowReturnsResultsInForkOrderAlthoughALaterForkFinishedFirst() throws Exception {
        var secondDone = new CountDownLatch(1);
        Callable<String> first = () -> {
            Await.latch(secondDone);                    // the first fork finishes last
            return "shard-0";
        };
        Callable<String> second = () -> {
            secondDone.countDown();
            return "shard-1";
        };
        assertThat(ScatterGather.gatherAll(List.of(first, second))).containsExactly("shard-0", "shard-1");
    }

    @Test
    void allSuccessfulOrThrowFailsWhenOneShardFails() {
        var failure = new IllegalStateException("shard down");
        assertThatThrownBy(() -> ScatterGather.gatherAll(List.<Callable<String>>of(() -> "ok", () -> {
            throw failure;
        }))).isInstanceOf(ExecutionException.class).hasCause(failure);
    }

    @Test
    void bestEffortReturnsOnlyTheSuccessesInForkOrder() throws InterruptedException {
        List<Callable<String>> shards = List.of(() -> "a", () -> {
            throw new IllegalStateException("b down");
        }, () -> "c");
        assertThat(ScatterGather.gatherBestEffort(shards, Await.BOUND)).containsExactly("a", "c");
    }

    @Test
    void bestEffortOnTimeoutReturnsTheSuccessesSoFarAndCancelsTheRest() throws InterruptedException {
        try (var scope = StructuredTaskScope.open(new BestEffortJoiner<String>(),
                config -> config.withTimeout(Duration.ofMillis(50)))) {
            var a = scope.fork(() -> "a");
            scope.fork(() -> {
                throw new IllegalStateException("b down");
            });
            var c = scope.fork(() -> "c");
            awaitSuccess(a);                            // make "so far" deterministic: a and c are done
            awaitSuccess(c);
            scope.fork(() -> hang(new CountDownLatch(1), new CountDownLatch(1)));   // only the timeout ends it
            assertThat(scope.join()).containsExactly("a", "c");
            assertThat(scope.isCancelled()).isTrue();
        }
    }

    @Test
    void demoPrintsOneLinePerJoiningPolicy() {
        assertThat(Demos.output(() -> MirrorDownloadDemo.main(new String[0]))).isEqualTo("""
                anySuccessfulOrThrow: Download[mirror=us-fast, content=report.pdf from us-fast]
                  the slow mirror was interrupted: true
                anySuccessfulOrThrow, every mirror broken: ExecutionException caused by IOException
                allSuccessfulOrThrow, fork order: [shard-0: 3 hits, shard-1: 5 hits, shard-2: 0 hits]
                best effort, one shard failing: [shard-0: 3 hits, shard-2: 0 hits]
                best effort, 50 ms timeout, one shard failing and one hanging: []
                """);
    }
}
