package io.github.aliturgutbozkurt.patterns.m10.examples.immutable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.config.ConfigSnapshot;
import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.config.LiveConfig;
import io.github.aliturgutbozkurt.patterns.m10.support.Await;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class LiveConfigTest {

    private static final ConfigSnapshot INITIAL = new ConfigSnapshot(1, 1, 2, Map.of("checkout.v2", "off"));

    @Test
    void sixteenWritersTimesAThousandUpdatesLoseNoUpdate() throws Exception {
        var config = new LiveConfig(new ConfigSnapshot(0, 1, 2, Map.of()));
        var start = new CountDownLatch(1);
        List<Future<?>> writers = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int w = 0; w < 16; w++) {
                writers.add(executor.submit(() -> {
                    Await.latch(start);
                    for (int i = 0; i < 1_000; i++) {
                        config.update(s -> s.withVersion(s.version() + 1));   // pure function: safe to retry
                    }
                    return null;
                }));
            }
            start.countDown();
            for (Future<?> writer : writers) {
                writer.get(5, TimeUnit.SECONDS);
            }
        }
        assertThat(config.current().version()).isEqualTo(16_000);
    }

    @Test
    void readersNeverSeeAHalfUpdatedSnapshotWhileWritersChangeBothFieldsTogether() throws Exception {
        var config = new LiveConfig(INITIAL);          // writers keep max == 2 * min; a torn read would break it
        var start = new CountDownLatch(1);
        var torn = new AtomicInteger();
        List<Future<?>> tasks = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int w = 0; w < 4; w++) {
                tasks.add(executor.submit(() -> {
                    Await.latch(start);
                    for (int k = 1; k <= 2_000; k++) {
                        int min = k;
                        config.update(s -> s.withPoolSize(min, 2 * min));
                    }
                    return null;
                }));
            }
            for (int r = 0; r < 8; r++) {
                tasks.add(executor.submit(() -> {
                    Await.latch(start);
                    for (int i = 0; i < 5_000; i++) {
                        ConfigSnapshot seen = config.current();              // one read = one consistent snapshot
                        if (seen.minConnections() > seen.maxConnections()
                                || seen.maxConnections() != 2 * seen.minConnections()) {
                            torn.incrementAndGet();
                        }
                    }
                    return null;
                }));
            }
            start.countDown();
            for (Future<?> task : tasks) {
                task.get(5, TimeUnit.SECONDS);
            }
        }
        assertThat(torn.get()).isZero();
    }

    @Test
    void snapshotFlagsAreACopyAndUnmodifiable() {
        var flags = new HashMap<>(Map.of("a", "on"));
        var snapshot = new ConfigSnapshot(1, 1, 2, flags);
        flags.put("b", "on");
        assertThat(snapshot.flags()).containsOnlyKeys("a");
        assertThatThrownBy(() -> snapshot.flags().put("c", "on")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void updateFunctionThatThrowsLeavesTheCurrentSnapshotUnchanged() {
        var config = new LiveConfig(INITIAL);
        var failure = new IllegalStateException("bad reload");
        assertThatThrownBy(() -> config.update(_ -> {
            throw failure;
        })).isSameAs(failure);
        assertThatThrownBy(() -> config.update(s -> s.withPoolSize(50, 40)))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("minConnections 50 > maxConnections 40");
        assertThat(config.current()).isSameAs(INITIAL);
    }

    @Test
    void witherBumpsNothingElseAndReturnsANewSnapshot() {
        var next = INITIAL.withFlag("checkout.v2", "on");
        assertThat(next.flags()).containsEntry("checkout.v2", "on");
        assertThat(INITIAL.flags()).containsEntry("checkout.v2", "off");
        assertThat(next.version()).isEqualTo(INITIAL.version());
        assertThatThrownBy(() -> new ConfigSnapshot(-1, 1, 2, Map.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ConfigSnapshot(1, -1, 2, Map.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void demoPrintsSnapshotsAndTheConcurrentResult() {
        assertThat(Demos.output(() -> LiveConfigDemo.main(new String[0]))).isEqualTo("""
                v1: pool 5..20, flags {checkout.v2=off, search.fuzzy=on}
                v2: pool 10..40, flags {checkout.v2=off, search.fuzzy=on}
                v3: pool 10..40, flags {checkout.v2=on, search.fuzzy=on}
                flags().put(...) -> UnsupportedOperationException
                pool 50..40 rejected: minConnections 50 > maxConnections 40; current is still v3
                16 writers x 1000 updates on virtual threads -> v16003 (no update lost)
                """);
    }
}
