package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.dynamic;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/proxy/dynamic/DynamicProxyDemo.java} */
public final class DynamicProxyDemo {

    private DynamicProxyDemo() {}

    public static void main(String[] args) {
        var fakeNanos = new AtomicLong();
        LongSupplier ticker = () -> fakeNanos.addAndGet(3_000_000);   // every reading: +3 ms, no real clock
        Consumer<String> log = line -> System.out.println("  log: " + line);

        var repository = new InMemoryOrderRepository();
        repository.save("A-1", "2 x keyboard");
        repository.save("A-2", "1 x monitor");

        System.out.println("timing proxy:");
        OrderRepository timed = Proxies.timed(OrderRepository.class, repository, ticker, log);
        System.out.println("  findById -> " + timed.findById("A-1"));
        System.out.println("  describe -> " + timed.describe("A-2"));
        try {
            timed.findById("Z-9");
        } catch (NoSuchElementException e) {                    // the real exception, not a wrapper
            System.out.println("  findById -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        PriceList prices = Proxies.timed(PriceList.class,
                new InMemoryPriceList(Map.of("SKU-1", new BigDecimal("49.90"))), ticker, log);
        System.out.println("  priceOf -> " + prices.priceOf("SKU-1"));

        System.out.println("read-only proxy:");
        OrderRepository readOnly = Proxies.readOnly(OrderRepository.class, repository);
        System.out.println("  findAllIds -> " + readOnly.findAllIds());
        try {
            readOnly.save("A-3", "3 x mouse");
        } catch (UnsupportedOperationException e) {
            System.out.println("  save -> " + e.getMessage());
        }
        System.out.println("  ids after the blocked save: " + repository.findAllIds());
        System.out.println("Proxy.isProxyClass: " + Proxy.isProxyClass(readOnly.getClass()));
    }
}
