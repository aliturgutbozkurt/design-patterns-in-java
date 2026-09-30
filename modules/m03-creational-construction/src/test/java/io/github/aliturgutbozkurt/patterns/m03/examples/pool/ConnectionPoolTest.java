package io.github.aliturgutbozkurt.patterns.m03.examples.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m03.support.Console;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class ConnectionPoolTest {

    static final Duration ONE_SECOND = Duration.ofSeconds(1);

    @Test
    void reusesConnectionsSequentially() throws InterruptedException {
        var pool = new ConnectionPool(3, FakeConnection::new);
        for (int i = 0; i < 5; i++) {
            try (PooledConnection connection = pool.acquire(ONE_SECOND)) {
                assertThat(connection.query("SELECT 1")).isEqualTo("conn-1: SELECT 1");
            }
        }
        assertThat(pool.created()).isEqualTo(1);
    }

    @Test
    void neverLendsMoreThanItsSizeUnderManyVirtualThreads() throws Exception {
        var pool = new ConnectionPool(3, FakeConnection::new);
        List<Future<String>> results = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 200; i++) {
                int n = i;
                results.add(executor.submit(() -> {
                    try (PooledConnection connection = pool.acquire(Duration.ofSeconds(10))) {
                        Thread.sleep(1);
                        return connection.query("q" + n);
                    }
                }));
            }
        }
        for (Future<String> result : results) {
            assertThat(result.get()).matches("conn-[123]: q\\d+");
        }
        assertThat(pool.maxInUse()).isBetween(1, 3);
        assertThat(pool.created()).isBetween(1, 3);
        assertThat(pool.inUse()).isZero();
    }

    @Test
    void timesOutWhenExhausted() throws InterruptedException {
        var pool = new ConnectionPool(1, FakeConnection::new);
        try (PooledConnection held = pool.acquire(ONE_SECOND)) {
            assertThat(held.query("x")).startsWith("conn-1");
            assertThatIllegalStateException().isThrownBy(() -> pool.acquire(Duration.ofMillis(50)))
                    .withMessage("no connection available within PT0.05S");
        }
    }

    @Test
    void closingTwiceIsHarmlessAndAClosedLeaseCannotBeUsed() throws InterruptedException {
        var pool = new ConnectionPool(1, FakeConnection::new);
        PooledConnection lease = pool.acquire(ONE_SECOND);
        lease.close();
        lease.close();
        assertThat(pool.inUse()).isZero();
        assertThatIllegalStateException().isThrownBy(() -> lease.query("x")).withMessage("lease already closed");
        try (PooledConnection again = pool.acquire(ONE_SECOND)) {
            assertThat(again.query("y")).isEqualTo("conn-1: y");
        }
    }

    @Test
    void demoPrintsOnlyOrderIndependentFacts() {
        assertThat(Console.capture(() -> {
            try {
                ConnectionPoolDemo.main(new String[0]);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        })).isEqualTo("""
                conn-1: SELECT 1
                conn-1: SELECT 2
                conn-1: SELECT 3
                sequential use created 1 connection(s)
                200 virtual threads, pool of 2: max in use <= 2? true, created <= 2? true
                exhausted: no connection available within PT0.05S
                """);
    }
}
