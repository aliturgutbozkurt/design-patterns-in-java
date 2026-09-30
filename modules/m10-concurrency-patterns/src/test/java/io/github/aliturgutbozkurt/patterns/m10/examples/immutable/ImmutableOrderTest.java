package io.github.aliturgutbozkurt.patterns.m10.examples.immutable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order.MutableOrder;
import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order.Money;
import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order.Order;
import io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order.OrderLine;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class ImmutableOrderTest {

    private static Money eur(long cents) {
        return Money.of(cents, "EUR");
    }

    private static final OrderLine BOOK = new OrderLine("book", 2, eur(20_00));
    private static final OrderLine MUG = new OrderLine("mug", 1, eur(12_50));

    @Test
    void mutatingTheSourceListAfterConstructionDoesNotChangeTheOrder() {
        var source = new ArrayList<>(List.of(BOOK));
        var order = new Order("A-1", source);
        source.add(MUG);
        source.set(0, MUG);
        assertThat(order.lines()).containsExactly(BOOK);
    }

    @Test
    void linesAreUnmodifiable() {
        var order = new Order("A-1", List.of(BOOK));
        assertThatThrownBy(() -> order.lines().add(MUG)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void withersReturnNewInstancesAndLeaveTheOriginalUnchanged() {
        var original = new Order("A-1", List.of(BOOK));
        var copyBefore = new Order("A-1", List.of(BOOK));
        var bigger = original.withLine(MUG);
        var smaller = bigger.withoutSku("book");
        assertThat(bigger.lines()).containsExactly(BOOK, MUG);
        assertThat(smaller.lines()).containsExactly(MUG);
        assertThat(original).isEqualTo(copyBefore).isNotSameAs(bigger);
    }

    @Test
    void totalIsTheSumOfTheLines() {
        assertThat(new Order("A-1", List.of(BOOK, MUG)).total()).isEqualTo(eur(52_50));
        assertThat(eur(52_50)).hasToString("52.50 EUR");
    }

    @Test
    void compactConstructorsRejectInvalidValues() {
        assertThatThrownBy(() -> new OrderLine("pen", -1, eur(1_00)))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("quantity must be positive: -1");
        assertThatThrownBy(() -> new OrderLine(" ", 1, eur(1_00))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Order("A-2", List.of(BOOK, new OrderLine("cap", 1, Money.of(9_00, "USD")))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("mixed currencies in order A-2: EUR and USD");
        assertThatThrownBy(() -> new Order("A-3", List.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Order("A-1", List.of(BOOK)).withoutSku("book"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> eur(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void thousandConcurrentReadersAllSeeAConsistentTotalWhileOthersDeriveNewOrders() {
        var shared = new Order("A-1", List.of(BOOK, MUG, new OrderLine("pen", 3, eur(1_50))));
        var consistent = new AtomicInteger();
        var derived = new AtomicInteger();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                int n = i;
                executor.submit(() -> {
                    if (n % 10 == 0) {                   // "updates" never touch the shared instance
                        var updated = shared.withLine(new OrderLine("extra-" + n, 1, eur(n)));
                        derived.addAndGet(updated.lines().size() == 4 ? 1 : 0);
                    }
                    long sum = shared.lines().stream().mapToLong(line -> line.total().cents()).sum();
                    if (shared.total().cents() == sum && sum == 57_00) {
                        consistent.incrementAndGet();
                    }
                });
            }
        }
        assertThat(consistent.get()).isEqualTo(1_000);
        assertThat(derived.get()).isEqualTo(100);
        assertThat(shared.lines()).hasSize(3);
    }

    @Test
    void mutableBeanLeaksItsInternalListToCallers() {
        var bean = new MutableOrder("A-1");
        bean.addLine(BOOK);
        bean.getLines().add(MUG);                        // nobody called a setter...
        assertThat(bean.getLines()).hasSize(2);          // ...but the bean changed
        assertThat(bean.getTotal()).isEqualTo(eur(52_50));
    }

    @Test
    void demoContrastsTheImmutableOrderWithTheMutableBean() {
        assertThat(Demos.output(() -> ImmutableOrderDemo.main(new String[0]))).isEqualTo("""
                order A-1: 3 lines, total 57.50 EUR
                withLine(pen) -> new order: 4 lines, total 60.00 EUR; original: 3 lines, total 57.50 EUR
                withoutSku(book) -> 2 lines; original unchanged: true
                source list changed after construction -> order still has 3 lines
                lines().add(...) -> UnsupportedOperationException
                new OrderLine("pen", -1, ...) -> quantity must be positive: -1
                EUR line + USD line -> mixed currencies in order A-2: EUR and USD
                1000 virtual threads read A-1 while others derive new orders -> all totals consistent: true
                MutableOrder: a caller added to getLines() -> the bean now has 4 lines, no setter was called
                """);
    }
}
