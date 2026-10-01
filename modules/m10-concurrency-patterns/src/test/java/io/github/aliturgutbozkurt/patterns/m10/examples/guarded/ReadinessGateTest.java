package io.github.aliturgutbozkurt.patterns.m10.examples.guarded;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.gate.ReadinessGate;
import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.gate.WarmingService;
import io.github.aliturgutbozkurt.patterns.m10.support.Await;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class ReadinessGateTest {

    /** A virtual thread waiting (bounded) for the gate; its result is {@code awaitReady}'s answer or exception. */
    private static Thread waiter(ReadinessGate gate, CompletableFuture<Boolean> result) {
        return Thread.ofVirtual().start(() -> {
            try {
                result.complete(gate.awaitReady(Await.BOUND));
            } catch (InterruptedException | RuntimeException e) {
                result.completeExceptionally(e);
            }
        });
    }

    @Test
    void waiterIsSuspendedUntilMarkReadyAndThenReturnsTrue() throws Exception {
        var gate = new ReadinessGate();
        var ready = new CompletableFuture<Boolean>();
        Await.untilBlocked(waiter(gate, ready));
        assertThat(ready).isNotDone();

        gate.markReady();
        assertThat(ready.get(5, TimeUnit.SECONDS)).isTrue();
        assertThat(gate.state()).isEqualTo(ReadinessGate.State.READY);
    }

    @Test
    void oneMarkReadyReleasesAHundredWaiters() throws Exception {
        var gate = new ReadinessGate();
        List<CompletableFuture<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            var result = new CompletableFuture<Boolean>();
            results.add(result);
            Await.untilBlocked(waiter(gate, result));
        }
        gate.markReady();
        for (var result : results) {
            assertThat(result.get(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Test
    void markFailedWakesEveryWaiterWithTheCause() throws Exception {
        var gate = new ReadinessGate();
        var cause = new IllegalStateException("database unreachable");
        List<CompletableFuture<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            var result = new CompletableFuture<Boolean>();
            results.add(result);
            Await.untilBlocked(waiter(gate, result));
        }
        gate.markFailed(cause);
        for (var result : results) {
            assertThatThrownBy(() -> result.get(5, TimeUnit.SECONDS))
                    .isInstanceOf(ExecutionException.class)
                    .cause().isInstanceOf(IllegalStateException.class).hasCause(cause);
        }
    }

    @Test
    void awaitReadyTimesOutWithFalseWhileStarting() throws InterruptedException {
        var gate = new ReadinessGate();
        assertThat(gate.awaitReady(Duration.ofMillis(50))).isFalse();
        assertThat(gate.state()).isEqualTo(ReadinessGate.State.STARTING);
    }

    @Test
    void awaitReadyReturnsAtOnceWhenAlreadyReady() throws InterruptedException {
        var gate = new ReadinessGate();
        gate.markReady();
        assertThat(gate.awaitReady(Duration.ZERO)).isTrue();
    }

    @Test
    void awaitReadyThrowsAtOnceWhenAlreadyFailed() {
        var gate = new ReadinessGate();
        var cause = new RuntimeException("boom");
        gate.markFailed(cause);
        assertThatThrownBy(() -> gate.awaitReady(Duration.ZERO)).isInstanceOf(IllegalStateException.class)
                .hasCause(cause);
    }

    @Test
    void rejectsTransitionsOutOfAFinalState() {
        var ready = new ReadinessGate();
        ready.markReady();
        assertThatThrownBy(() -> ready.markFailed(new RuntimeException())).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(ready::markReady).isInstanceOf(IllegalStateException.class);

        var failed = new ReadinessGate();
        failed.markFailed(new RuntimeException());
        assertThatThrownBy(failed::markReady).isInstanceOf(IllegalStateException.class);
        assertThat(failed.state()).isEqualTo(ReadinessGate.State.FAILED);
    }

    @Test
    void lookupsWaitForTheWarmUpAndThenAnswerFromTheCache() throws Exception {
        var service = new WarmingService(() -> Map.of("apple", "1.20 EUR"));
        var answer = new CompletableFuture<Optional<String>>();
        Thread request = Thread.ofVirtual().start(() -> {
            try {
                answer.complete(service.lookup("apple", Await.BOUND));
            } catch (InterruptedException e) {
                answer.completeExceptionally(e);
            }
        });
        Await.untilBlocked(request);
        service.warmUp();
        assertThat(answer.get(5, TimeUnit.SECONDS)).contains("1.20 EUR");
        assertThat(service.lookup("pear", Duration.ZERO)).isEmpty();
    }

    @Test
    void failedWarmUpFailsTheGateAndTheLookups() {
        var cause = new IllegalStateException("price database unreachable");
        var service = new WarmingService(() -> {
            throw cause;
        });
        assertThatThrownBy(service::warmUp).isSameAs(cause);
        assertThat(service.gate().state()).isEqualTo(ReadinessGate.State.FAILED);
        assertThatThrownBy(() -> service.lookup("apple", Duration.ZERO)).hasCause(cause);
    }

    @Test
    void lookupBeforeReadyTimesOut() {
        var service = new WarmingService(Map::of);
        assertThatThrownBy(() -> service.lookup("apple", Duration.ofMillis(50)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("not ready");
    }

    @Test
    void demoPrintsAnswersInRequestOrderAndTheFailure() {
        assertThat(Demos.output(() -> ReadinessGateDemo.main(new String[0]))).isEqualTo("""
                3 requests started before the cache was warm
                warm-up finished: READY
                GET apple -> Optional[1.20 EUR]
                GET bread -> Optional[2.50 EUR]
                GET milk -> Optional[0.99 EUR]
                awaitReady(50 ms) while STARTING: false
                failed start-up: IllegalStateException: service failed to start, cause: price database unreachable
                """);
    }
}
