package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.customer;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.item;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.money;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.orderId;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.sku;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderCancelled;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderDelivered;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderPaid;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderPlaced;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderShipped;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.StockLow;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.Subscription;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.Notification;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderView;
import io.github.aliturgutbozkurt.patterns.capstone.api.sim.SimulatedPaymentApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** F8 — domain events after commit, typed subscribers, notifications, low-stock alerts (brief §2.2). */
public abstract class EventsAcceptance extends AcceptanceContract {

    private <E extends ShopEvent> List<E> record(Class<E> type) {
        List<E> events = new ArrayList<>();
        shop().events().subscribe(type, events::add);
        return events;
    }

    private static List<String> subjects(List<Notification> notifications) {
        return notifications.stream().map(Notification::subject).toList();
    }

    @Test
    void checkoutPublishesOrderPlacedThenOrderPaid() {
        List<ShopEvent> events = record(ShopEvent.class);

        kit().placeOrder("alice", item("BOK-001", 2));

        assertThat(events).containsExactly(
                new OrderPlaced(orderId("order-1"), customer("alice"), money("499.90")),
                new OrderPaid(orderId("order-1"), "txn-1"));
    }

    @Test
    void typedSubscriberReceivesOnlyItsEventType() {
        List<OrderPaid> paid = new ArrayList<>();
        Subscription subscription = shop().events().subscribe(OrderPaid.class, paid::add);

        kit().placeOrder("alice", item("BOK-001", 1));
        assertThat(paid).containsExactly(new OrderPaid(orderId("order-1"), "txn-1"));

        subscription.close();
        subscription.close();
        kit().placeOrder("alice", item("BOK-001", 1));
        assertThat(paid).as("closed subscriptions receive nothing").hasSize(1);
    }

    @Test
    void supertypeSubscriberReceivesAllEvents() {
        List<ShopEvent> events = record(ShopEvent.class);

        OrderId first = kit().placeOrder("alice", item("BOK-001", 1));
        shop().fulfilment().fulfilPaidOrders();
        shop().orders().markDelivered(first);
        OrderId second = kit().placeOrder("bob", item("HOM-001", 1));
        shop().orders().cancel(second, "changed my mind");

        assertThat(events).containsExactly(
                new OrderPlaced(first, customer("alice"), money("274.90")),
                new OrderPaid(first, "txn-1"),
                new OrderShipped(first, "TRK-0001"),
                new OrderDelivered(first),
                new OrderPlaced(second, customer("bob"), money("139.80")),
                new OrderPaid(second, "txn-2"),
                new OrderCancelled(second, "changed my mind", true));

        kit().placeOrder("carol", item("TOY-001", 2));
        assertThat(events.subList(7, events.size())).containsExactlyInAnyOrder(
                new OrderPlaced(orderId("order-3"), customer("carol"), money("289.90")),
                new OrderPaid(orderId("order-3"), "txn-3"),
                new StockLow(sku("TOY-001"), 4));
    }

    @Test
    void subscribersSeeCommittedState() {
        List<Optional<OrderStatus>> statusWhenPaid = new ArrayList<>();
        List<Integer> stockWhenPaid = new ArrayList<>();
        List<String> trackingWhenShipped = new ArrayList<>();
        shop().events().subscribe(OrderPaid.class, event -> {
            statusWhenPaid.add(shop().orders().find(event.order()).map(OrderView::status));
            stockWhenPaid.add(kit().stock("TOY-001"));
        });
        shop().events().subscribe(OrderShipped.class, event ->
                trackingWhenShipped.add(kit().order(event.order()).trackingCode()));

        kit().placeOrder("alice", item("TOY-001", 3));
        shop().fulfilment().fulfilPaidOrders();

        assertThat(statusWhenPaid).containsExactly(Optional.of(OrderStatus.PAID));
        assertThat(stockWhenPaid).containsExactly(3);
        assertThat(trackingWhenShipped).containsExactly("TRK-0001");
    }

    @Test
    void rejectedCheckoutPublishesAndNotifiesNothing() {
        List<ShopEvent> events = record(ShopEvent.class);

        kit().checkout(shop().carts().open(customer("alice")));
        shop().checkout().checkout(new CheckoutRequest(kit().cartWith("alice", item("TOY-001", 5)),
                ShopTestKit.address(), SimulatedPaymentApi.DECLINED));

        assertThat(events).isEmpty();
        assertThat(kit().notifications().all()).isEmpty();
        assertThat(kit().errors().all()).isEmpty();
    }

    @Test
    void customerIsNotifiedOnConfirmationShipmentAndCancellation() {
        OrderId first = kit().placeOrder("alice", item("BOK-001", 2));
        shop().fulfilment().fulfilPaidOrders();
        OrderId second = kit().placeOrder("alice", item("HOM-001", 1));
        shop().orders().cancel(second, "found it cheaper");

        List<Notification> toAlice = kit().notifications().to("alice");
        assertThat(subjects(toAlice)).containsExactly(
                "Order order-1 confirmed", "Order order-1 shipped", "Order order-2 confirmed", "Order order-2 cancelled");
        assertThat(toAlice.get(0).body()).contains("499.90");
        assertThat(toAlice.get(1).body()).contains("TRK-0001");
        assertThat(toAlice.get(3).body()).contains("found it cheaper");
        assertThat(first).isEqualTo(orderId("order-1"));
    }

    @Test
    void failingSubscriberIsReportedAndOthersStillRun() {
        IllegalStateException bug = new IllegalStateException("subscriber bug");
        shop().events().subscribe(OrderPlaced.class, _ -> {
            throw bug;
        });
        List<ShopEvent> events = record(ShopEvent.class);

        CheckoutResult result = kit().checkout(kit().cartWith("alice", item("BOK-001", 1)));

        assertThat(result).isEqualTo(new CheckoutResult.Placed(orderId("order-1"), money("274.90"), "txn-1"));
        assertThat(kit().errors().all()).containsExactly(bug);
        assertThat(events).hasExactlyElementsOfTypes(OrderPlaced.class, OrderPaid.class);
        assertThat(subjects(kit().notifications().to("alice"))).containsExactly("Order order-1 confirmed");
        assertThat(kit().order(orderId("order-1")).status()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void stockLowIsPublishedOncePerThresholdCrossing() {
        List<StockLow> lows = record(StockLow.class);

        kit().placeOrder("bob", item("TOY-001", 1));
        assertThat(lows).as("6 → 5 is not below 5").isEmpty();
        kit().placeOrder("bob", item("TOY-001", 1));
        assertThat(lows).containsExactly(new StockLow(sku("TOY-001"), 4));
        kit().placeOrder("bob", item("TOY-001", 1));
        assertThat(lows).as("already low: no new alert").hasSize(1);

        shop().catalogue().restock(sku("TOY-001"), 5);
        kit().placeOrder("bob", item("TOY-001", 4));
        assertThat(lows).containsExactly(new StockLow(sku("TOY-001"), 4), new StockLow(sku("TOY-001"), 4));

        List<Notification> toOps = kit().notifications().to("ops");
        assertThat(toOps).hasSize(2);
        assertThat(toOps).allSatisfy(n -> assertThat(n.subject() + " " + n.body()).contains("TOY-001"));
    }
}
