package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.customer;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.item;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.orderId;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderView;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.StatusChange;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.TransitionResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.TransitionResult.Done;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.TransitionResult.Refused;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

/** F6, F7 — the order lifecycle: PLACED → PAID → SHIPPED → DELIVERED, PAID → CANCELLED with refund (brief §2.2). */
public abstract class LifecycleAcceptance extends AcceptanceContract {

    private OrderUseCase orders() {
        return shop().orders();
    }

    private static List<OrderStatus> statuses(OrderView order) {
        return order.history().stream().map(StatusChange::status).toList();
    }

    private static OrderView done(TransitionResult result) {
        assertThat(result).isInstanceOf(Done.class);
        return ((Done) result).order();
    }

    @Test
    void placedOrderHistoryIsPlacedThenPaidWithClockTimes() {
        kit().clock().advance(Duration.ofMinutes(90));
        Instant now = kit().clock().instant();

        OrderView order = kit().order(kit().placeOrder("alice", item("BOK-001", 1)));

        assertThat(order.placedAt()).isEqualTo(now);
        assertThat(order.history()).containsExactly(
                new StatusChange(OrderStatus.PLACED, now, ""),
                new StatusChange(OrderStatus.PAID, now, "txn-1"));
    }

    @Test
    void cancelPaidOrderRefundsRestocksAndRecordsReason() {
        OrderId id = kit().placeOrder("alice", item("TOY-001", 3), item("DIG-001", 1));
        assertThat(kit().stock("TOY-001")).isEqualTo(3);
        assertThat(orders().cancel(id, " ")).isEqualTo(new Refused("missing reason"));
        kit().clock().advance(Duration.ofHours(2));

        OrderView cancelled = done(orders().cancel(id, "changed my mind"));

        assertThat(cancelled.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(cancelled.history().getLast())
                .isEqualTo(new StatusChange(OrderStatus.CANCELLED, kit().clock().instant(), "changed my mind"));
        assertThat(kit().order(id)).isEqualTo(cancelled);
        assertThat(kit().payments().calls("refund")).containsExactly(new SandboxPaymentApi.Call("refund",
                List.of("PATTERNSHOP", "txn-1", cancelled.total().toPlainString(), "TRY")));
        assertThat(kit().stock("TOY-001")).isEqualTo(6);
    }

    @Test
    void refundFailureRefusesCancellation() {
        OrderId id = kit().placeOrder("alice", item("TOY-001", 2));
        kit().payments().failRefunds();

        assertThat(orders().cancel(id, "changed my mind")).isEqualTo(new Refused("refund failed"));
        assertThat(kit().order(id).status()).isEqualTo(OrderStatus.PAID);
        assertThat(kit().order(id).history()).hasSize(2);
        assertThat(kit().stock("TOY-001")).isEqualTo(4);
    }

    @Test
    void cannotCancelShippedOrDeliveredOrder() {
        OrderId id = kit().placeOrder("alice", item("BOK-001", 1));
        shop().fulfilment().fulfilPaidOrders();

        assertThat(orders().cancel(id, "too late")).isEqualTo(new Refused("cannot cancel SHIPPED order"));
        done(orders().markDelivered(id));
        assertThat(orders().cancel(id, "too late")).isEqualTo(new Refused("cannot cancel DELIVERED order"));
        assertThat(kit().payments().calls("refund")).isEmpty();
    }

    @Test
    void deliverOnlyFromShipped() {
        OrderId id = kit().placeOrder("alice", item("BOK-001", 1));
        assertThat(orders().markDelivered(id)).isEqualTo(new Refused("cannot deliver PAID order"));

        shop().fulfilment().fulfilPaidOrders();
        kit().clock().advance(Duration.ofDays(2));
        OrderView delivered = done(orders().markDelivered(id));

        assertThat(delivered.status()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(statuses(delivered))
                .containsExactly(OrderStatus.PLACED, OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.DELIVERED);
        assertThat(delivered.history().getLast().at()).isEqualTo(kit().clock().instant());
        assertThat(orders().markDelivered(id)).isEqualTo(new Refused("cannot deliver DELIVERED order"));
    }

    @Test
    void cancelledOrderRefusesEveryTransition() {
        OrderId id = kit().placeOrder("alice", item("BOK-001", 1));
        done(orders().cancel(id, "changed my mind"));

        assertThat(orders().cancel(id, "again")).isEqualTo(new Refused("cannot cancel CANCELLED order"));
        assertThat(orders().markDelivered(id)).isEqualTo(new Refused("cannot deliver CANCELLED order"));
        assertThat(shop().fulfilment().fulfilPaidOrders().shipped()).isEmpty();
        assertThat(kit().order(id).status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void unknownOrderIsRefused() {
        assertThat(orders().cancel(orderId("order-9"), "why not")).isEqualTo(new Refused("unknown order: order-9"));
        assertThat(orders().markDelivered(orderId("order-9"))).isEqualTo(new Refused("unknown order: order-9"));
        assertThat(orders().find(orderId("order-9"))).isEmpty();
    }

    @Test
    void ordersOfCustomerAreInPlacementOrder() {
        OrderId first = kit().placeOrder("alice", item("BOK-001", 1));
        kit().placeOrder("bob", item("TOY-001", 1));
        OrderId third = kit().placeOrder("alice", item("HOM-001", 2));

        assertThat(orders().ordersOf(customer("alice"))).extracting(OrderView::id).containsExactly(first, third);
        assertThat(orders().ordersOf(customer("carol"))).isEmpty();
    }
}
