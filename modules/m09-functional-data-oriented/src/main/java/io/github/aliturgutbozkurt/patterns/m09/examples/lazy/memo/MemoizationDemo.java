package io.github.aliturgutbozkurt.patterns.m09.examples.lazy.memo;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/lazy/memo/MemoizationDemo.java}
 *
 * @see "m09 lesson, section Lazy evaluation and memoisation"
 */
public final class MemoizationDemo {

    private MemoizationDemo() {}

    public static void main(String[] args) {
        System.out.println("-- a memoised supplier: nothing happens until get()");
        var rates = new ExchangeRates();
        Supplier<BigDecimal> euroRate = Memoized.supplier(() -> rates.load("EUR/TRY"));
        System.out.println("created (loads so far: " + rates.loads() + ")");
        System.out.println("get() -> " + euroRate.get() + " (loads: " + rates.loads() + ")");
        System.out.println("get() -> " + euroRate.get() + " (loads: " + rates.loads() + ")");

        System.out.println("-- a memoised function: one load per distinct key");
        var moreRates = new ExchangeRates();
        Function<String, BigDecimal> rate = Memoized.function(moreRates::load);
        var line = new StringBuilder();
        for (String pair : List.of("EUR/TRY", "USD/TRY", "EUR/TRY", "USD/TRY")) {
            line.append(pair).append('=').append(rate.apply(pair)).append(' ');
        }
        System.out.println(line + "(loads: " + moreRates.loads() + ")");

        System.out.println("-- a failure is not cached: the next get() tries again");
        var attempts = new AtomicInteger();
        Supplier<BigDecimal> flaky = Memoized.supplier(() -> {
            if (attempts.incrementAndGet() == 1) {
                throw new IllegalStateException("rate service down");
            }
            return rates.load("EUR/TRY");
        });
        for (int i = 0; i < 2; i++) {
            try {
                System.out.println("get() -> " + flaky.get());
            } catch (IllegalStateException e) {
                System.out.println("get() -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        System.out.println("-- the recursive HashMap.computeIfAbsent trap");
        try {
            new Fibonacci().recursiveWithComputeIfAbsent(10);
        } catch (ConcurrentModificationException e) {
            System.out.println("fib(10), recursive computeIfAbsent -> " + e.getClass().getSimpleName());
        }
        System.out.println("fib(90), iterative                 -> " + Fibonacci.iterative(90));
    }
}
