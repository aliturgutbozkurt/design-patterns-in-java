package io.github.aliturgutbozkurt.patterns.m07.examples.observer;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.AuditLog;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.EventBus;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.OrderPlaced;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.OrderShipped;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.PaymentFailed;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus.ShopEvent;
import java.math.BigDecimal;

/**
 * Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/observer/EventBusDemo.java}
 *
 * @see "m07 lesson, section Observer"
 */
public final class EventBusDemo {

    private EventBusDemo() {}

    public static void main(String[] args) {
        var bus = new EventBus();
        bus.publish(new OrderPlaced("A-0", "alan", new BigDecimal("10.00"))); // nobody listens yet: a dead event

        // The warehouse reacts to one event type by publishing another one.
        bus.subscribe(OrderPlaced.class, placed -> {
            System.out.println("warehouse: shipping " + placed.orderId());
            bus.publish(new OrderShipped(placed.orderId(), "TRK-" + placed.orderId())); // queued, not recursive
        });
        bus.subscribe(ShopEvent.class, new AuditLog(line -> System.out.println("audit:     " + line)));
        bus.subscribe(PaymentFailed.class, failed -> System.out.println(
                "mailer:    e-mail sent: payment for " + failed.orderId() + " failed (" + failed.reason() + ")"));

        bus.publish(new OrderPlaced("A-1", "ada", new BigDecimal("42.00")));
        bus.publish(new PaymentFailed("A-2", "card declined"));
        System.out.println("dead events: " + bus.deadEvents());
    }
}
