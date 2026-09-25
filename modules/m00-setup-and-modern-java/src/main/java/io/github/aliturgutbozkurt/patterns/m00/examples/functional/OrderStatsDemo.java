package io.github.aliturgutbozkurt.patterns.m00.examples.functional;

import io.github.aliturgutbozkurt.patterns.m00.examples.records.Money;
import java.util.List;

/** Run: {@code java modules/m00-setup-and-modern-java/src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/functional/OrderStatsDemo.java} */
public final class OrderStatsDemo {

    private OrderStatsDemo() {}

    public static void main(String[] args) {
        List<OrderLine> lines = List.of(
                new OrderLine("Keyboard", "hardware", 2, Money.of("49.90", "EUR")),
                new OrderLine("Patterns book", "books", 3, Money.of("39.00", "EUR")),
                new OrderLine("Mouse", "hardware", 5, Money.of("19.99", "EUR")),
                new OrderLine("Java 27 guide", "books", 1, Money.of("29.00", "EUR")));

        System.out.println("revenue by category: " + OrderStats.revenueByCategory(lines));
        OrderStats.bestSeller(lines).ifPresent(line ->
                System.out.println("best seller: " + line.product() + " (" + line.quantity() + " pcs)"));
        System.out.println("batches of 3: " + OrderStats.batches(lines, 3).stream()
                .map(batch -> batch.stream().map(OrderLine::product).toList())
                .toList());
        System.out.println("newest first: " + OrderStats.newestFirst(lines));
    }
}
