package io.github.aliturgutbozkurt.patterns.m00.examples.functional;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m00.examples.records.Money;
import io.github.aliturgutbozkurt.patterns.m00.support.Console;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderStatsTest {

    private final List<OrderLine> lines = List.of(
            new OrderLine("Keyboard", "hardware", 2, Money.of("49.90", "EUR")),
            new OrderLine("Patterns book", "books", 3, Money.of("39.00", "EUR")),
            new OrderLine("Mouse", "hardware", 5, Money.of("19.99", "EUR")),
            new OrderLine("Java 27 guide", "books", 1, Money.of("29.00", "EUR")));

    @Test
    void sumsRevenuePerCategorySortedByName() {
        assertThat(OrderStats.revenueByCategory(lines)).containsExactly(
                org.assertj.core.api.Assertions.entry("books", Money.of("146.00", "EUR")),
                org.assertj.core.api.Assertions.entry("hardware", Money.of("199.75", "EUR")));
    }

    @Test
    void findsTheBestSellerByQuantity() {
        assertThat(OrderStats.bestSeller(lines)).map(OrderLine::product).contains("Mouse");
        assertThat(OrderStats.bestSeller(List.of())).isEmpty();
    }

    @Test
    void splitsLinesIntoFixedSizeBatches() {
        assertThat(OrderStats.batches(lines, 3)).extracting(List::size).containsExactly(3, 1);
    }

    @Test
    void listsProductsNewestFirst() {
        assertThat(OrderStats.newestFirst(lines))
                .containsExactly("Java 27 guide", "Mouse", "Patterns book", "Keyboard");
    }

    @Test
    void demoPrintsExpectedLines() {
        assertThat(Console.capture(() -> OrderStatsDemo.main(new String[0]))).isEqualTo("""
                revenue by category: {books=146.00 EUR, hardware=199.75 EUR}
                best seller: Mouse (5 pcs)
                batches of 3: [[Keyboard, Patterns book, Mouse], [Java 27 guide]]
                newest first: [Java 27 guide, Mouse, Patterns book, Keyboard]
                """);
    }
}
