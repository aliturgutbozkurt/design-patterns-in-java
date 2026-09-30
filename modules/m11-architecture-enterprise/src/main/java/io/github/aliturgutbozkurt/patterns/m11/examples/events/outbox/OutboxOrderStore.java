package io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox;

import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent.OrderCancelled;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent.OrderPaid;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent.OrderPlaced;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent.OrderShipped;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.SequencedMap;

/**
 * Transactional outbox: the order state and its events are written <em>together</em> — one assignment of a new
 * immutable state, the in-memory equivalent of one database transaction. Either both are stored or neither is.
 *
 * @see "m11 lesson, section Domain events — transactional outbox"
 */
public final class OutboxOrderStore {

    private record State(SequencedMap<String, OrderStatus> orders, List<OutboxEntry> outbox) {}

    private State state = new State(new LinkedHashMap<>(), List.of());
    private long lastSequence;
    private boolean failNextWrite;

    /** Makes the next {@link #save} fail before anything is written. */
    public void failNextWrite() {
        failNextWrite = true;
    }

    /** Stores the order's state and appends its pending events to the outbox, atomically. */
    public void save(Order order) {
        Objects.requireNonNull(order, "order");
        if (failNextWrite) {
            failNextWrite = false;
            throw new IllegalStateException("disk full"); // the order keeps its events: nothing was lost
        }
        SequencedMap<String, OrderStatus> orders = new LinkedHashMap<>(state.orders());
        orders.put(order.id(), order.status());
        List<OutboxEntry> outbox = new ArrayList<>(state.outbox());
        long sequence = lastSequence;
        for (OrderEvent event : order.pullEvents()) {
            outbox.add(new OutboxEntry(++sequence, event.getClass().getSimpleName(), payload(event), false));
        }
        state = new State(orders, List.copyOf(outbox)); // the "transaction": state and events in one step
        lastSequence = sequence;
    }

    /** Entries not yet published, in sequence order. */
    public List<OutboxEntry> pending() {
        return state.outbox().stream().filter(entry -> !entry.published()).toList();
    }

    /** Called by the relay once the broker accepted {@code sequence}. */
    public void markPublished(long sequence) {
        state = new State(state.orders(), state.outbox().stream()
                .map(entry -> entry.sequence() == sequence ? entry.asPublished() : entry)
                .toList());
    }

    /** Stored order statuses (an unmodifiable copy). */
    public SequencedMap<String, OrderStatus> orders() {
        return Collections.unmodifiableSequencedMap(new LinkedHashMap<>(state.orders()));
    }

    /** Every outbox entry, published or not. */
    public List<OutboxEntry> outbox() {
        return state.outbox();
    }

    private static String payload(OrderEvent event) {
        return switch (event) {
            case OrderPlaced(String id, long totalCents) -> id + " " + totalCents;
            case OrderPaid(String id) -> id;
            case OrderShipped(String id) -> id;
            case OrderCancelled(String id, String reason) -> id + " " + reason;
        };
    }
}
