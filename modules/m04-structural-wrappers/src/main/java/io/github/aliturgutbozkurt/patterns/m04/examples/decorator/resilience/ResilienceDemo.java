package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.resilience;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/decorator/resilience/ResilienceDemo.java} */
public final class ResilienceDemo {

    private ResilienceDemo() {}

    public static void main(String[] args) {
        Map<String, Integer> stock = Map.of("A-42", 7);
        Consumer<String> log = line -> System.out.println("  log: " + line);

        // Composition root: the same three pieces, stacked in two orders.
        System.out.println("logging(retrying(flaky)):");
        StockService loggingOutside =
                new LoggingStockService(new RetryingStockService(new FlakyStockService(2, stock), 3), log);
        loggingOutside.available("A-42");

        System.out.println("retrying(logging(flaky)):");
        StockService retryingOutside =
                new RetryingStockService(new LoggingStockService(new FlakyStockService(2, stock), log), 3);
        retryingOutside.available("A-42");

        System.out.println("retrying(flaky) that never recovers:");
        try {
            new RetryingStockService(new FlakyStockService(5, stock), 3).available("A-42");
        } catch (IllegalStateException e) {
            String suppressed = Arrays.stream(e.getSuppressed())
                    .map(Throwable::getMessage)
                    .collect(Collectors.joining(", "));
            System.out.println("  gave up: " + e.getMessage() + " (suppressed: " + suppressed + ")");
        }
    }
}
