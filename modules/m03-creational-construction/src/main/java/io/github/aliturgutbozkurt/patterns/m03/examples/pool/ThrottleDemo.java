package io.github.aliturgutbozkurt.patterns.m03.examples.pool;

import io.github.aliturgutbozkurt.patterns.m03.examples.pool.throttle.ThrottledClient;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/** Run: {@code java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/pool/ThrottleDemo.java} */
public final class ThrottleDemo {

    private ThrottleDemo() {}

    public static void main(String[] args) {
        var client = new ThrottledClient(5, request -> "ok:" + request);
        var completed = new AtomicInteger();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {  // one new virtual thread per task
            for (int i = 0; i < 1_000; i++) {
                int n = i;
                executor.submit(() -> {
                    client.call("r" + n);
                    completed.incrementAndGet();
                    return null;
                });
            }
        }
        System.out.println("1000 calls on 1000 virtual threads completed: " + (completed.get() == 1_000));
        System.out.println("peak concurrent calls <= 5? " + (client.peakConcurrency() <= 5));
    }
}
