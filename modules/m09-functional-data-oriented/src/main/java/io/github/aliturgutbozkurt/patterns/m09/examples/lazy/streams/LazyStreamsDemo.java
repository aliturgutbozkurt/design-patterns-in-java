package io.github.aliturgutbozkurt.patterns.m09.examples.lazy.streams;

import java.lang.System.Logger.Level;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/lazy/streams/LazyStreamsDemo.java} */
public final class LazyStreamsDemo {

    private LazyStreamsDemo() {}

    public static void main(String[] args) {
        System.out.println("-- vertical, one element at a time; findFirst stops early");
        var tracer = new LazyTrace();
        var first = tracer.firstEven(List.of(1, 2, 3, 4, 5));
        System.out.println(tracer.trace() + " -> " + first);

        System.out.println("-- no terminal operation, no work");
        var idle = new LazyTrace();
        idle.withoutTerminalOperation(List.of(1, 2, 3));
        System.out.println("trace without a terminal operation: " + idle.trace());

        System.out.println("-- an infinite stream, cut by limit");
        System.out.println("Stream.iterate(1, x -> 2 * x).limit(5) = " + LazyTrace.powersOfTwo(5));

        System.out.println("-- a message supplier runs only if the level is enabled");
        var built = new AtomicInteger();
        var log = new LazyLog(Level.INFO, System.out::println);
        log.log(Level.DEBUG, () -> {
            built.incrementAndGet();
            return "cart dump: " + List.of("MUG-0001", "TEE-0002");
        });
        log.log(Level.WARNING, () -> {
            built.incrementAndGet();
            return "cart CART-7 has 3 items";
        });
        System.out.println("message suppliers called: " + built.get());

        System.out.println("-- running balance (scan) vs. closing balance (fold)");
        List<Long> movements = List.of(100L, -30L, 5L);
        System.out.println("movements " + movements);
        System.out.println("scan -> " + Ledger.runningBalances(movements));
        System.out.println("fold -> " + Ledger.closingBalance(movements));
    }
}
