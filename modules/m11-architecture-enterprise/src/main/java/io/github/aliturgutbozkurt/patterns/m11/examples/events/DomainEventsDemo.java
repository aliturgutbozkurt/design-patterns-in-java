package io.github.aliturgutbozkurt.patterns.m11.examples.events;

import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.DomainEventDispatcher;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderStore;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.ReserveStock;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.SendConfirmation;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.UnitOfWork;
import java.util.function.Consumer;

/**
 * Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/events/DomainEventsDemo.java}
 *
 * @see "m11 lesson, section Domain events"
 */
public final class DomainEventsDemo {

    private DomainEventsDemo() {}

    public static void main(String[] args) {
        Consumer<String> out = System.out::println;
        var dispatcher = new DomainEventDispatcher<OrderEvent>(error -> out.accept("error handler: " + error.getMessage()));
        dispatcher.subscribe(OrderEvent.OrderPlaced.class, new SendConfirmation(out));
        dispatcher.subscribe(OrderEvent.OrderPaid.class, new ReserveStock(out));
        dispatcher.subscribe(OrderEvent.class, event -> out.accept("audit: " + event));
        var store = new OrderStore();
        var unitOfWork = new UnitOfWork(store, dispatcher);

        Order order = Order.place("order-1", 4700);
        order.pay();
        unitOfWork.register(order);
        out.accept("recorded, not committed yet: nothing dispatched");
        unitOfWork.commit(); // save first, then dispatch
        out.accept("stored: " + store.statuses());

        unitOfWork.register(Order.place("order-2", 1200));
        store.failNextSave();
        try {
            unitOfWork.commit();
        } catch (IllegalStateException e) {
            out.accept("commit failed (" + e.getMessage() + "): stored " + store.statuses() + ", nothing dispatched");
            unitOfWork.rollback();
        }

        Order loaded = store.load("order-1").orElseThrow();
        loaded.cancel("customer changed their mind");
        try {
            loaded.pay();
        } catch (IllegalStateException e) {
            out.accept("illegal transition: " + e.getMessage());
        }
        unitOfWork.register(loaded);
        unitOfWork.commit();
        out.accept("stored: " + store.statuses());
    }
}
