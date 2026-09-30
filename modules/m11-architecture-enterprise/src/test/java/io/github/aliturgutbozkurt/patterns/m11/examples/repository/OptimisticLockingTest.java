package io.github.aliturgutbozkurt.patterns.m11.examples.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders.ConcurrentUpdateException;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders.InMemoryOrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders.OrderLine;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import org.junit.jupiter.api.Test;

class OptimisticLockingTest {

    private final InMemoryOrderRepository orders = new InMemoryOrderRepository();

    private Order savedOrder() {
        Order order = new Order(orders.nextId());
        orders.save(order);
        return order;
    }

    @Test
    void nextIdYieldsSequentialIds() {
        assertThat(orders.nextId()).isEqualTo("order-1");
        assertThat(orders.nextId()).isEqualTo("order-2");
    }

    @Test
    void saveIncrementsTheVersion() {
        Order order = new Order("order-1");
        assertThat(order.version()).isZero();
        orders.save(order);
        assertThat(order.version()).isEqualTo(1);
        order.addLine("BOOK-1", 1);
        orders.save(order);
        assertThat(orders.findById("order-1").orElseThrow().version()).isEqualTo(2);
    }

    @Test
    void editsToALoadedCopyAreInvisibleUntilSave() {
        savedOrder();
        Order copy = orders.findById("order-1").orElseThrow();
        copy.addLine("BOOK-1", 2);
        assertThat(orders.findById("order-1").orElseThrow().lines()).isEmpty();
        orders.save(copy);
        assertThat(orders.findById("order-1").orElseThrow().lines()).containsExactly(new OrderLine("BOOK-1", 2));
    }

    @Test
    void secondOfTwoSavesFromTheSameVersionFailsAndTheFirstWins() {
        savedOrder();
        Order ada = orders.findById("order-1").orElseThrow();
        Order alan = orders.findById("order-1").orElseThrow();
        ada.addLine("BOOK-1", 2);
        alan.addLine("PEN-7", 1);
        orders.save(ada);

        ConcurrentUpdateException conflict = catchThrowableOfType(ConcurrentUpdateException.class,
                () -> orders.save(alan));

        assertThat(conflict).hasMessage("order-1 was changed concurrently: expected version 1 but found 2");
        assertThat(conflict.expectedVersion()).isEqualTo(1);
        assertThat(conflict.actualVersion()).isEqualTo(2);
        Order stored = orders.findById("order-1").orElseThrow();
        assertThat(stored.lines()).containsExactly(new OrderLine("BOOK-1", 2));
        assertThat(stored.version()).isEqualTo(2);
    }

    @Test
    void reloadingAfterAConflictAllowsARetry() {
        savedOrder();
        Order ada = orders.findById("order-1").orElseThrow();
        Order alan = orders.findById("order-1").orElseThrow();
        ada.addLine("BOOK-1", 2);
        orders.save(ada);
        alan.addLine("PEN-7", 1);
        catchThrowableOfType(ConcurrentUpdateException.class, () -> orders.save(alan));

        Order retry = orders.findById("order-1").orElseThrow();
        retry.addLine("PEN-7", 1);
        orders.save(retry);
        assertThat(orders.findById("order-1").orElseThrow().version()).isEqualTo(3);
    }

    @Test
    void unknownIdIsEmptyAndInvalidInputIsRejected() {
        assertThat(orders.findById("order-9")).isEmpty();
        assertThatNullPointerException().isThrownBy(() -> orders.save(null));
        assertThatIllegalArgumentException().isThrownBy(() -> new Order("order-1").addLine("BOOK-1", 0));
    }

    @Test
    void demoPrintsTheLostUpdateThatWasPrevented() {
        assertThat(Console.capture(() -> OptimisticLockingDemo.main(new String[0]))).isEqualTo("""
                created order-1 at version 1
                ada and alan both load order-1 at version 1
                ada adds BOOK-1 x 2 and saves: version 2
                alan adds PEN-7 x 1 and saves: order-1 was changed concurrently: expected version 1 but found 2
                stored: [BOOK-1 x 2] at version 2
                alan reloads, re-applies and saves: [BOOK-1 x 2, PEN-7 x 1] at version 3
                """);
    }
}
