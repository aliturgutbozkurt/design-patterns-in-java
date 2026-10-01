package io.github.aliturgutbozkurt.patterns.m11.examples.events;

import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox.IdempotentConsumer;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox.MessageBroker;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox.OutboxEntry;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox.OutboxOrderStore;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox.OutboxRelay;
import java.util.ArrayList;
import java.util.List;

/**
 * Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/events/OutboxDemo.java}
 *
 * @see "m11 lesson, section Domain events"
 */
public final class OutboxDemo {

    private OutboxDemo() {}

    public static void main(String[] args) {
        var store = new OutboxOrderStore();
        var relay = new OutboxRelay(store);
        List<String> processed = new ArrayList<>();
        var consumer = new IdempotentConsumer(entry -> processed.add(entry.toString()));

        Order order = Order.place("order-1", 4700);
        order.pay();
        store.save(order);
        System.out.println("saved order-1 with its events: pending " + sequences(store.pending()));

        store.failNextWrite();
        try {
            store.save(Order.place("order-2", 1200));
        } catch (IllegalStateException e) {
            System.out.println("write failed (" + e.getMessage() + "): orders " + store.orders()
                    + ", outbox entries " + store.outbox().size());
        }

        MessageBroker down = entry -> {
            throw new IllegalStateException("broker unavailable");
        };
        relay(relay, down, store);

        // The broker delivers entry 1 but the relay crashes before marking it: it will be sent again.
        boolean[] crashOnce = {true};
        MessageBroker flaky = entry -> {
            consumer.receive(entry);
            if (crashOnce[0]) {
                crashOnce[0] = false;
                throw new IllegalStateException("relay crashed before marking " + entry.sequence());
            }
        };
        relay(relay, flaky, store);
        relay(relay, flaky, store);

        System.out.println("consumer processed: " + processed + ", duplicates ignored: " + consumer.duplicates());
    }

    private static void relay(OutboxRelay relay, MessageBroker broker, OutboxOrderStore store) {
        try {
            System.out.println("relayed " + relay.relayPending(broker) + ": pending " + sequences(store.pending()));
        } catch (IllegalStateException e) {
            System.out.println("relay failed (" + e.getMessage() + "): pending " + sequences(store.pending()));
        }
    }

    private static List<Long> sequences(List<OutboxEntry> entries) {
        return entries.stream().map(OutboxEntry::sequence).toList();
    }
}
