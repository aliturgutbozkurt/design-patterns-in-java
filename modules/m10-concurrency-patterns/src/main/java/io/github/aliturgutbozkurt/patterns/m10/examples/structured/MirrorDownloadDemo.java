package io.github.aliturgutbozkurt.patterns.m10.examples.structured;

import io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors.Mirror;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors.MirrorDownloader;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors.ScatterGather;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Run (preview API, JEP 533): {@code java --enable-preview --source 27 modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/structured/MirrorDownloadDemo.java}
 *
 * <p>Choosing a joiner is choosing a policy. Slow and hanging work waits on latches, so the output never changes.
 */
public final class MirrorDownloadDemo {

    private MirrorDownloadDemo() {}

    public static void main(String[] args) throws Exception {
        var slowStarted = new CountDownLatch(1);
        var slowInterrupted = new CountDownLatch(1);
        Mirror slow = new Mirror("eu-slow", _ -> {
            slowStarted.countDown();
            try {
                new CountDownLatch(1).await();                  // cancelled once another mirror wins
                return "never";
            } catch (InterruptedException e) {
                slowInterrupted.countDown();
                throw e;
            }
        });
        Mirror fast = new Mirror("us-fast", file -> {
            slowStarted.await();                                // win once the slow mirror is running
            return file + " from us-fast";
        });
        Mirror broken = new Mirror("asia-broken", _ -> {
            throw new IOException("connection refused");
        });

        System.out.println("anySuccessfulOrThrow: "
                + new MirrorDownloader(List.of(slow, fast, broken)).download("report.pdf"));
        System.out.println("  the slow mirror was interrupted: " + (slowInterrupted.getCount() == 0));
        try {
            new MirrorDownloader(List.of(broken, broken)).download("report.pdf");
        } catch (ExecutionException e) {
            System.out.println("anySuccessfulOrThrow, every mirror broken: ExecutionException caused by "
                    + e.getCause().getClass().getSimpleName());
        }

        var shard1Done = new CountDownLatch(1);
        Callable<String> shard0 = () -> {
            shard1Done.await();                                 // finishes after shard 1
            return "shard-0: 3 hits";
        };
        Callable<String> shard1 = () -> {
            shard1Done.countDown();
            return "shard-1: 5 hits";
        };
        Callable<String> shard2 = () -> "shard-2: 0 hits";
        Callable<String> failing = () -> {
            throw new IllegalStateException("shard-1 down");
        };
        Callable<String> hanging = () -> {
            new CountDownLatch(1).await();
            return "never";
        };
        System.out.println("allSuccessfulOrThrow, fork order: " + ScatterGather.gatherAll(List.of(shard0, shard1,
                shard2)));
        System.out.println("best effort, one shard failing: "
                + ScatterGather.gatherBestEffort(List.of(() -> "shard-0: 3 hits", failing, shard2),
                        Duration.ofSeconds(5)));
        System.out.println("best effort, 50 ms timeout, one shard failing and one hanging: "
                + ScatterGather.gatherBestEffort(List.of(failing, hanging), Duration.ofMillis(50)));
    }
}
