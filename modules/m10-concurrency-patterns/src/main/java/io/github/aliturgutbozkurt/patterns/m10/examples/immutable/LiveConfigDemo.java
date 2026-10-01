package io.github.aliturgutbozkurt.patterns.m10.examples.immutable;

import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.config.ConfigSnapshot;
import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.config.LiveConfig;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/immutable/LiveConfigDemo.java}
 *
 * @see "m10 lesson, section Immutable Object"
 */
public final class LiveConfigDemo {

    private LiveConfigDemo() {}

    public static void main(String[] args) {
        var config = new LiveConfig(new ConfigSnapshot(1, 5, 20, Map.of("checkout.v2", "off", "search.fuzzy", "on")));
        print(config.current());
        print(config.update(s -> s.withPoolSize(10, 40).withVersion(s.version() + 1)));
        print(config.update(s -> s.withFlag("checkout.v2", "on").withVersion(s.version() + 1)));

        try {
            config.current().flags().put("checkout.v2", "off");
        } catch (UnsupportedOperationException e) {
            System.out.println("flags().put(...) -> UnsupportedOperationException");
        }
        try {
            config.update(s -> s.withPoolSize(50, 40).withVersion(s.version() + 1));
        } catch (IllegalArgumentException e) {
            System.out.println("pool 50..40 rejected: " + e.getMessage() + "; current is still v"
                    + config.current().version());
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int writer = 0; writer < 16; writer++) {
                executor.submit(() -> {
                    for (int i = 0; i < 1_000; i++) {
                        config.update(s -> s.withVersion(s.version() + 1));
                    }
                });
            }
        }
        System.out.println("16 writers x 1000 updates on virtual threads -> v" + config.current().version()
                + " (no update lost)");
    }

    private static void print(ConfigSnapshot s) {
        System.out.println("v" + s.version() + ": pool " + s.minConnections() + ".." + s.maxConnections()
                + ", flags " + new TreeMap<>(s.flags()));      // sorted: Map.copyOf has no defined order
    }
}
