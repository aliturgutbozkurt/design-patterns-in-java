package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.item;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.orderId;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.ShopSettings;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderShipped;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentFailure;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentReport;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.StatusChange;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/**
 * F9 — concurrent fulfilment on virtual threads with a parallelism limit (brief §2.2 "Fulfilment"). Concurrency is
 * proven with barriers and latches in the {@link ScriptedWarehouse}, never with timing.
 */
public abstract class FulfilmentAcceptance extends AcceptanceContract {

    private List<OrderId> placeOrders(int count, String sku) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> kit().placeOrder("customer" + i, item(sku, 1)))
                .toList();
    }

    private static List<String> operations(List<ScriptedWarehouse.Call> calls) {
        return calls.stream().map(call -> call.operation() + " " + String.join(" ", call.arguments()).strip()).toList();
    }

    @Test
    void shipsEveryPaidOrderWithTrackingCode() {
        OrderId first = kit().placeOrder("alice", item("BOK-001", 2));
        OrderId second = kit().placeOrder("bob", item("HOM-001", 1), item("TOY-001", 1));
        List<OrderShipped> events = new ArrayList<>();
        shop().events().subscribe(OrderShipped.class, events::add);

        FulfilmentReport report = shop().fulfilment().fulfilPaidOrders();

        assertThat(report).isEqualTo(new FulfilmentReport(List.of(first, second), List.of()));
        assertThat(kit().order(first).status()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(kit().order(first).trackingCode()).isEqualTo("TRK-0001");
        assertThat(kit().order(second).trackingCode()).isEqualTo("TRK-0002");
        assertThat(kit().order(first).history().getLast())
                .isEqualTo(new StatusChange(OrderStatus.SHIPPED, kit().clock().instant(), "TRK-0001"));
        assertThat(operations(kit().warehouse().callsFor("order-2")))
                .containsExactly("pick HOM-001 1", "pick TOY-001 1", "pack", "ship PCL-2 34710");
        assertThat(events).containsExactly(new OrderShipped(first, "TRK-0001"), new OrderShipped(second, "TRK-0002"));
    }

    @Test
    void fulfilsOrdersConcurrently() {
        List<OrderId> orders = List.of(
                kit().placeOrder("alice", item("BOK-001", 1)),
                kit().placeOrder("bob", item("HOM-001", 1)),
                kit().placeOrder("carol", item("ELE-001", 1)));
        kit().warehouse().holdPicksAt(new CyclicBarrier(3)); // only reachable if all three pick at the same time

        FulfilmentReport report = shop().fulfilment().fulfilPaidOrders();

        assertThat(report.failed()).isEmpty();
        assertThat(report.shipped()).isEqualTo(orders);
    }

    @Test
    void neverExceedsMaxParallelOrders() {
        kitWith(new ShopSettings("PATTERNSHOP", 2, 5));
        List<OrderId> orders = placeOrders(8, "HOM-001");
        kit().warehouse().holdPicksAt(new CyclicBarrier(2)); // pairs must meet: proves 2 at once, never more

        FulfilmentReport report = shop().fulfilment().fulfilPaidOrders();

        assertThat(report.failed()).isEmpty();
        assertThat(report.shipped()).isEqualTo(orders);
        assertThat(kit().warehouse().peakOrdersInFlight()).isEqualTo(2);
    }

    @Test
    void warehouseIsCalledFromVirtualThreads() {
        placeOrders(3, "HOM-001");

        shop().fulfilment().fulfilPaidOrders();

        assertThat(kit().warehouse().calls()).hasSize(9).allMatch(ScriptedWarehouse.Call::virtualThread);
    }

    @Test
    void failingOrderStaysPaidAndOthersShip() {
        List<OrderId> orders = placeOrders(3, "HOM-001");
        kit().warehouse().failOrder("order-2", "label printer jammed");

        FulfilmentReport report = shop().fulfilment().fulfilPaidOrders();

        assertThat(report).isEqualTo(new FulfilmentReport(List.of(orders.get(0), orders.get(2)),
                List.of(new FulfilmentFailure(orders.get(1), "label printer jammed"))));
        assertThat(kit().order(orders.get(1)).status()).isEqualTo(OrderStatus.PAID);
        assertThat(kit().order(orders.get(1)).trackingCode()).isEmpty();
        assertThat(kit().order(orders.get(2)).status()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void resultsAndEventsAreInOrderNumberOrder() {
        kitWith(new ShopSettings("PATTERNSHOP", 10, 5));
        List<OrderId> orders = placeOrders(10, "HOM-001");
        ScriptedWarehouse warehouse = kit().warehouse();
        warehouse.holdOrderUntil("order-1", warehouse.shippedSignal("order-10"));
        warehouse.holdOrderUntil("order-2", warehouse.shippedSignal("order-1"));
        List<OrderShipped> events = new CopyOnWriteArrayList<>();
        List<Thread> handlerThreads = new CopyOnWriteArrayList<>();
        shop().events().subscribe(OrderShipped.class, event -> {
            events.add(event);
            handlerThreads.add(Thread.currentThread());
        });

        FulfilmentReport report = shop().fulfilment().fulfilPaidOrders();

        List<String> shipOrder = warehouse.calls().stream()
                .filter(call -> call.operation().equals("ship")).map(ScriptedWarehouse.Call::orderRef).toList();
        assertThat(shipOrder.indexOf("order-1")).as("completion order was scrambled")
                .isGreaterThan(shipOrder.indexOf("order-10")).isLessThan(shipOrder.indexOf("order-2"));
        assertThat(report.shipped()).isEqualTo(orders);
        assertThat(events).extracting(OrderShipped::order).isEqualTo(orders);
        assertThat(handlerThreads).as("events are dispatched on the calling thread").containsOnly(Thread.currentThread());
    }

    @Test
    void digitalOnlyOrderShipsWithoutWarehouse() {
        OrderId digital = kit().placeOrder("alice", item("DIG-001", 1));
        OrderId mixed = kit().placeOrder("bob", item("BOK-001", 1), item("DIG-001", 2));

        FulfilmentReport report = shop().fulfilment().fulfilPaidOrders();

        assertThat(report.shipped()).containsExactly(digital, mixed);
        assertThat(kit().order(digital).trackingCode()).isEqualTo("DIGITAL");
        assertThat(kit().order(digital).status()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(kit().warehouse().callsFor("order-1")).isEmpty();
        assertThat(operations(kit().warehouse().callsFor("order-2")))
                .as("only physical lines are picked").containsExactly("pick BOK-001 1", "pack", "ship PCL-2 34710");
        assertThat(kit().order(mixed).trackingCode()).isEqualTo("TRK-0002");
    }

    @Test
    void secondRunShipsNothingAndCancelledOrdersAreSkipped() {
        OrderId kept = kit().placeOrder("alice", item("BOK-001", 1));
        OrderId cancelled = kit().placeOrder("bob", item("HOM-001", 1));
        shop().orders().cancel(cancelled, "changed my mind");

        assertThat(shop().fulfilment().fulfilPaidOrders()).isEqualTo(new FulfilmentReport(List.of(kept), List.of()));
        assertThat(kit().warehouse().callsFor("order-2")).isEmpty();
        assertThat(shop().fulfilment().fulfilPaidOrders()).isEqualTo(new FulfilmentReport(List.of(), List.of()));
        assertThat(kit().order(cancelled).status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(kit().warehouse().calls()).hasSize(3);
        assertThat(orderId("order-1")).isEqualTo(kept);
    }
}
