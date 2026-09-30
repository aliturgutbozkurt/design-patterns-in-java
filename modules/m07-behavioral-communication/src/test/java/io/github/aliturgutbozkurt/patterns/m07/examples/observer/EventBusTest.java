package io.github.aliturgutbozkurt.patterns.m07.examples.observer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.AuditLog;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.EventBus;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.OrderPlaced;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.OrderShipped;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.PaymentFailed;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.ShopEvent;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.modern.Subscription;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EventBusTest {

    private static final OrderPlaced PLACED = new OrderPlaced("A-1", "ada", new BigDecimal("42.00"));
    private static final PaymentFailed FAILED = new PaymentFailed("A-2", "card declined");
    private static final OrderShipped SHIPPED = new OrderShipped("A-1", "TRK-A-1");

    private final EventBus bus = new EventBus();
    private final List<String> received = new ArrayList<>();

    @Test
    void handlerReceivesOnlyItsEventType() {
        List<PaymentFailed> failures = new ArrayList<>();
        bus.subscribe(PaymentFailed.class, failures::add);
        bus.publish(PLACED);
        bus.publish(FAILED);
        bus.publish(SHIPPED);
        assertThat(failures).containsExactly(FAILED);
    }

    @Test
    void supertypeHandlerReceivesAllEvents() {
        List<ShopEvent> all = new ArrayList<>();
        bus.subscribe(ShopEvent.class, all::add);
        bus.publish(PLACED);
        bus.publish(FAILED);
        bus.publish(SHIPPED);
        assertThat(all).containsExactly(PLACED, FAILED, SHIPPED);
    }

    @Test
    void deliversInSubscriptionOrder() {
        bus.subscribe(ShopEvent.class, event -> received.add("first"));
        bus.subscribe(OrderPlaced.class, event -> received.add("second"));
        bus.subscribe(ShopEvent.class, event -> received.add("third"));
        bus.publish(PLACED);
        assertThat(received).containsExactly("first", "second", "third");
    }

    @Test
    void eventsWithoutSubscriberAreKeptAsDeadEvents() {
        bus.subscribe(OrderPlaced.class, event -> received.add(event.orderId()));
        bus.publish(PLACED);
        bus.publish(FAILED);
        assertThat(received).containsExactly("A-1");
        assertThat(bus.deadEvents()).containsExactly(FAILED);
        assertThatThrownBy(() -> bus.deadEvents().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void closedSubscriptionReceivesNothing() {
        Subscription subscription = bus.subscribe(OrderPlaced.class, event -> received.add(event.orderId()));
        subscription.close();
        bus.publish(PLACED);
        assertThat(received).isEmpty();
        assertThat(bus.deadEvents()).containsExactly(PLACED);
    }

    @Test
    void eventPublishedFromAHandlerIsDeliveredAfterTheCurrentEventFinishes() {
        bus.subscribe(OrderPlaced.class, placed -> {
            received.add("warehouse ships " + placed.orderId());
            bus.publish(new OrderShipped(placed.orderId(), "TRK-" + placed.orderId()));
            received.add("warehouse done");
        });
        bus.subscribe(ShopEvent.class, event -> received.add("audit " + event.getClass().getSimpleName()));
        bus.publish(PLACED);
        assertThat(received).containsExactly(
                "warehouse ships A-1", "warehouse done", "audit OrderPlaced", "audit OrderShipped");
    }

    @Test
    void auditLogDescribesEveryEventType() {
        var audit = new AuditLog(received::add);
        bus.subscribe(ShopEvent.class, audit);
        bus.publish(PLACED);
        bus.publish(FAILED);
        bus.publish(SHIPPED);
        assertThat(received).containsExactly(
                "order A-1 placed by ada, total 42.00",
                "payment failed for A-2: card declined",
                "order A-1 shipped, tracking TRK-A-1");
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> bus.publish(null));
        assertThatNullPointerException().isThrownBy(() -> bus.subscribe(null, event -> {}));
        assertThatNullPointerException().isThrownBy(() -> bus.subscribe(OrderPlaced.class, null));
    }

    @Test
    void demoPrintsQueuedDeliveryAndDeadEvents() {
        assertThat(Console.capture(() -> EventBusDemo.main(new String[0]))).isEqualTo("""
                warehouse: shipping A-1
                audit:     order A-1 placed by ada, total 42.00
                audit:     order A-1 shipped, tracking TRK-A-1
                audit:     payment failed for A-2: card declined
                mailer:    e-mail sent: payment for A-2 failed (card declined)
                dead events: [OrderPlaced[orderId=A-0, customer=alan, total=10.00]]
                """);
    }
}
