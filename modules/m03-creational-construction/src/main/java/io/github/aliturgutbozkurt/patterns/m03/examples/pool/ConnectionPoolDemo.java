package io.github.aliturgutbozkurt.patterns.m03.examples.pool;

import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * Run: {@code java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/pool/ConnectionPoolDemo.java}
 * — the concurrent part prints only facts that do not depend on thread scheduling.
 */
public final class ConnectionPoolDemo {

    private ConnectionPoolDemo() {}

    public static void main(String[] args) throws InterruptedException {
        var sequential = new ConnectionPool(2, FakeConnection::new);
        for (int i = 1; i <= 3; i++) {
            try (PooledConnection connection = sequential.acquire(Duration.ofSeconds(1))) {
                System.out.println(connection.query("SELECT " + i));
            }
        }
        System.out.println("sequential use created " + sequential.created() + " connection(s)");

        var shared = new ConnectionPool(2, FakeConnection::new);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 200; i++) {
                executor.submit(() -> {
                    try (PooledConnection connection = shared.acquire(Duration.ofSeconds(10))) {
                        return connection.query("SELECT now()");
                    }
                });
            }
        }
        System.out.println("200 virtual threads, pool of 2: max in use <= 2? " + (shared.maxInUse() <= 2)
                + ", created <= 2? " + (shared.created() <= 2));

        var tiny = new ConnectionPool(1, FakeConnection::new);
        try (PooledConnection held = tiny.acquire(Duration.ofSeconds(1))) {
            held.query("keep it busy");
            tiny.acquire(Duration.ofMillis(50));
        } catch (IllegalStateException e) {
            System.out.println("exhausted: " + e.getMessage());
        }
    }
}
