package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState.Cancelled;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState.Delivered;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState.Paid;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState.Placed;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState.Shipped;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Transition.Allowed;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Transition.Refused;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class OrderLifecycleTest {

    private static final Instant T0 = Instant.parse("2026-11-16T06:00:00Z");

    private static Order placed() {
        return Order.builder().id(OrderId.of(1)).customer(new CustomerId("alice"))
                .item(new OrderItem(new Sku("BOK-001"), "Book", ProductType.PHYSICAL, 1, Money.of("250.00")))
                .total(Money.of("274.90")).shipTo(new Address("A", "B", "C", "D")).placedAt(T0).build();
    }

    @Test
    void happyPathCarriesOnlyTheDataOfEachState() {
        Transition pay = OrderLifecycle.pay(new Placed(), "txn-1");
        Transition ship = OrderLifecycle.ship(new Paid("txn-1"), "TRK-0001");
        Transition deliver = OrderLifecycle.deliver(new Shipped("txn-1", "TRK-0001"));

        assertThat(pay).isEqualTo(new Allowed(new Paid("txn-1"), "txn-1"));
        assertThat(ship).isEqualTo(new Allowed(new Shipped("txn-1", "TRK-0001"), "TRK-0001"));
        assertThat(deliver).isEqualTo(new Allowed(new Delivered("txn-1", "TRK-0001"), ""));
    }

    @Test
    void forbiddenTransitionsAreRefusedWithTheCurrentStatus() {
        assertThat(OrderLifecycle.cancel(new Shipped("txn-1", "TRK-0001"), "late"))
                .isEqualTo(new Refused("cannot cancel SHIPPED order"));
        assertThat(OrderLifecycle.deliver(new Paid("txn-1"))).isEqualTo(new Refused("cannot deliver PAID order"));
        assertThat(OrderLifecycle.ship(new Cancelled("txn-1", "why"), "TRK-0001"))
                .isEqualTo(new Refused("cannot ship CANCELLED order"));
        assertThat(OrderLifecycle.pay(new Paid("txn-1"), "txn-2")).isEqualTo(new Refused("cannot pay PAID order"));
    }

    @Test
    void cancellingNeedsAReasonAndKnowsWhetherMoneyWasRefunded() {
        assertThat(OrderLifecycle.cancel(new Paid("txn-1"), " ")).isEqualTo(new Refused("missing reason"));
        assertThat(OrderLifecycle.cancel(new Paid("txn-1"), "changed my mind"))
                .isEqualTo(new Allowed(new Cancelled("txn-1", "changed my mind"), "changed my mind"));
        assertThat(new Cancelled("txn-1", "x").refunded()).isTrue();
        assertThat(new Cancelled(OrderState.FREE, "x").refunded()).isFalse();
    }

    @Test
    void orderRecordsEveryAllowedTransitionInItsHistory() {
        Order order = placed();
        Order paid = order.after((Allowed) OrderLifecycle.pay(order.state(), "txn-1"), T0);
        Order shipped = paid.after((Allowed) OrderLifecycle.ship(paid.state(), "TRK-0001"), T0.plusSeconds(60));

        assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
        assertThat(shipped.status()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(shipped.state().trackingCode()).isEqualTo("TRK-0001");
        assertThat(shipped.state().paymentReference()).isEqualTo("txn-1");
        assertThat(shipped.history()).containsExactly(
                new HistoryEntry(OrderStatus.PLACED, T0, ""),
                new HistoryEntry(OrderStatus.PAID, T0, "txn-1"),
                new HistoryEntry(OrderStatus.SHIPPED, T0.plusSeconds(60), "TRK-0001"));
    }
}
