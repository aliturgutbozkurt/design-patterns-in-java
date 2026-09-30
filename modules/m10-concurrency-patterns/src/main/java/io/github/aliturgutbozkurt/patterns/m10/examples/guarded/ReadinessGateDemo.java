package io.github.aliturgutbozkurt.patterns.m10.examples.guarded;

import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.gate.ReadinessGate;
import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.gate.WarmingService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/guarded/ReadinessGateDemo.java}
 *
 * <p>Requests started before the warm-up wait at the gate; the answers are printed in request order.
 */
public final class ReadinessGateDemo {

    private ReadinessGateDemo() {}

    public static void main(String[] args) throws Exception {
        var service = new WarmingService(() -> Map.of("apple", "1.20 EUR", "bread", "2.50 EUR", "milk", "0.99 EUR"));
        List<String> keys = List.of("apple", "bread", "milk");
        List<CompletableFuture<Optional<String>>> answers = new ArrayList<>();
        for (String key : keys) {
            var answer = new CompletableFuture<Optional<String>>();
            answers.add(answer);
            Thread.ofVirtual().start(() -> {
                try {
                    answer.complete(service.lookup(key, Duration.ofSeconds(5)));   // suspends at the gate
                } catch (InterruptedException | RuntimeException e) {
                    answer.completeExceptionally(e);
                }
            });
        }
        System.out.println(keys.size() + " requests started before the cache was warm");

        service.warmUp();
        System.out.println("warm-up finished: " + service.gate().state());
        for (int i = 0; i < keys.size(); i++) {
            System.out.println("GET " + keys.get(i) + " -> " + answers.get(i).get());
        }

        System.out.println("awaitReady(50 ms) while STARTING: "
                + new ReadinessGate().awaitReady(Duration.ofMillis(50)));

        var broken = new ReadinessGate();
        broken.markFailed(new IllegalStateException("price database unreachable"));
        try {
            broken.awaitReady(Duration.ofSeconds(5));
        } catch (IllegalStateException e) {
            System.out.println("failed start-up: IllegalStateException: " + e.getMessage()
                    + ", cause: " + e.getCause().getMessage());
        }
    }
}
