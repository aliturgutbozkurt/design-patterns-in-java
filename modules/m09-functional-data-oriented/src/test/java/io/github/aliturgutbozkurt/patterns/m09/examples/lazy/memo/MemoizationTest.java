package io.github.aliturgutbozkurt.patterns.m09.examples.lazy.memo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class MemoizationTest {

    @Test
    void nothingIsComputedUntilTheFirstGet() {
        var calls = new AtomicInteger();
        Supplier<String> lazy = Memoized.supplier(() -> "config#" + calls.incrementAndGet());
        assertThat(calls).hasValue(0);
        assertThat(lazy.get()).isEqualTo("config#1");
        assertThat(lazy.get()).isEqualTo("config#1");
        assertThat(calls).hasValue(1);
    }

    @Test
    void theSupplierRunsExactlyOnceWhen100VirtualThreadsCallGet() throws InterruptedException, ExecutionException {
        var calls = new AtomicInteger();
        Supplier<Integer> lazy = Memoized.supplier(() -> {
            Thread.yield();
            return calls.incrementAndGet();
        });
        List<Future<Integer>> results = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100; i++) {
                results.add(executor.submit(lazy::get));
            }
        }
        for (Future<Integer> result : results) {
            assertThat(result.get()).isEqualTo(1);
        }
        assertThat(calls).hasValue(1);
    }

    @Test
    void anExceptionIsRethrownAndNotCached() {
        var calls = new AtomicInteger();
        Supplier<String> flaky = Memoized.supplier(() -> {
            if (calls.incrementAndGet() == 1) {
                throw new IllegalStateException("rate service down");
            }
            return "ok";
        });
        assertThatIllegalStateException().isThrownBy(flaky::get).withMessage("rate service down");
        assertThat(flaky.get()).isEqualTo("ok");
        assertThat(flaky.get()).isEqualTo("ok");
        assertThat(calls).hasValue(2);
    }

    @Test
    void aNullResultIsRejected() {
        Supplier<String> none = Memoized.supplier(() -> null);
        assertThatNullPointerException().isThrownBy(none::get).withMessage("the supplier returned null");
    }

    @Test
    void theMemoisedFunctionLoadsOncePerDistinctKey() {
        var rates = new ExchangeRates();
        Function<String, BigDecimal> rate = Memoized.function(rates::load);
        for (String pair : List.of("EUR/TRY", "USD/TRY", "EUR/TRY", "EUR/TRY", "USD/TRY")) {
            rate.apply(pair);
        }
        assertThat(rates.loads()).isEqualTo(2);
        assertThat(rate.apply("EUR/TRY")).isEqualByComparingTo("37.50");
    }

    @Test
    void recursiveComputeIfAbsentThrowsConcurrentModificationException() {
        assertThatThrownBy(() -> new Fibonacci().recursiveWithComputeIfAbsent(10))
                .isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    void iterativeFibonacciIsCorrect() {
        assertThat(Fibonacci.iterative(0)).isZero();
        assertThat(Fibonacci.iterative(1)).isEqualTo(1);
        assertThat(Fibonacci.iterative(10)).isEqualTo(55);
        assertThat(Fibonacci.iterative(90)).isEqualTo(2880067194370816120L);
    }

    @Test
    void demoPrintsLazinessMemoisationAndTheRecursionTrap() {
        assertThat(Console.capture(() -> MemoizationDemo.main(new String[0]))).isEqualTo("""
                -- a memoised supplier: nothing happens until get()
                created (loads so far: 0)
                get() -> 37.50 (loads: 1)
                get() -> 37.50 (loads: 1)
                -- a memoised function: one load per distinct key
                EUR/TRY=37.50 USD/TRY=34.20 EUR/TRY=37.50 USD/TRY=34.20 (loads: 2)
                -- a failure is not cached: the next get() tries again
                get() -> IllegalStateException: rate service down
                get() -> 37.50
                -- the recursive HashMap.computeIfAbsent trap
                fib(10), recursive computeIfAbsent -> ConcurrentModificationException
                fib(90), iterative                 -> 2880067194370816120
                """);
    }
}
