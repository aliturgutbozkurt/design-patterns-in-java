package io.github.aliturgutbozkurt.patterns.m03.examples.pool;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m03.examples.pool.throttle.ThrottledClient;
import io.github.aliturgutbozkurt.patterns.m03.support.Console;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

class ThrottledClientTest {

    @Test
    void boundsConcurrencyWithoutPoolingThreads() {
        var client = new ThrottledClient(5, request -> {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            return "ok:" + request;
        });
        var responses = new ConcurrentLinkedQueue<String>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                int n = i;
                executor.submit(() -> {
                    responses.add(client.call("r" + n));
                    return null;
                });
            }
        }
        assertThat(responses).hasSize(1_000).allMatch(response -> response.startsWith("ok:r"));
        assertThat(client.peakConcurrency()).isBetween(1, 5);
    }

    @Test
    void demoPrintsOnlyOrderIndependentFacts() {
        assertThat(Console.capture(() -> ThrottleDemo.main(new String[0]))).isEqualTo("""
                1000 calls on 1000 virtual threads completed: true
                peak concurrent calls <= 5? true
                """);
    }
}
