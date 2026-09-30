package io.github.aliturgutbozkurt.patterns.m10.examples.immutable;

import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order.Money;
import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order.MutableOrder;
import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order.Order;
import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order.OrderLine;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/immutable/ImmutableOrderDemo.java}
 */
public final class ImmutableOrderDemo {

    private ImmutableOrderDemo() {}

    public static void main(String[] args) {
        var book = new OrderLine("book", 2, Money.of(20_00, "EUR"));
        var mug = new OrderLine("mug", 1, Money.of(12_50, "EUR"));
        var notebook = new OrderLine("notebook", 1, Money.of(5_00, "EUR"));
        var source = new ArrayList<>(List.of(book, mug, notebook));
        var order = new Order("A-1", source);
        System.out.println("order A-1: " + order.lines().size() + " lines, total " + order.total());

        var withPen = order.withLine(new OrderLine("pen", 1, Money.of(2_50, "EUR")));
        System.out.println("withLine(pen) -> new order: " + withPen.lines().size() + " lines, total "
                + withPen.total() + "; original: " + order.lines().size() + " lines, total " + order.total());
        var withoutBook = order.withoutSku("book");
        System.out.println("withoutSku(book) -> " + withoutBook.lines().size() + " lines; original unchanged: "
                + order.lines().contains(book));

        source.clear();
        System.out.println("source list changed after construction -> order still has "
                + order.lines().size() + " lines");
        try {
            order.lines().add(book);
        } catch (UnsupportedOperationException e) {
            System.out.println("lines().add(...) -> UnsupportedOperationException");
        }
        try {
            new OrderLine("pen", -1, Money.of(2_50, "EUR"));
        } catch (IllegalArgumentException e) {
            System.out.println("new OrderLine(\"pen\", -1, ...) -> " + e.getMessage());
        }
        try {
            new Order("A-2", List.of(book, new OrderLine("cap", 1, Money.of(9_00, "USD"))));
        } catch (IllegalArgumentException e) {
            System.out.println("EUR line + USD line -> " + e.getMessage());
        }

        var consistent = new AtomicInteger();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                int n = i;
                executor.submit(() -> {
                    order.withLine(new OrderLine("gift-" + n, 1, Money.of(n, "EUR")));   // a new order each time
                    long sum = order.lines().stream().mapToLong(line -> line.total().cents()).sum();
                    if (order.total().cents() == sum) {                               // no lock anywhere
                        consistent.incrementAndGet();
                    }
                });
            }
        }
        System.out.println("1000 virtual threads read A-1 while others derive new orders -> all totals consistent: "
                + (consistent.get() == 1_000));

        var bean = new MutableOrder("A-1");
        List.of(book, mug, notebook).forEach(bean::addLine);
        bean.getLines().add(new OrderLine("pen", 1, Money.of(2_50, "EUR")));      // no setter involved
        System.out.println("MutableOrder: a caller added to getLines() -> the bean now has "
                + bean.getLines().size() + " lines, no setter was called");
    }
}
