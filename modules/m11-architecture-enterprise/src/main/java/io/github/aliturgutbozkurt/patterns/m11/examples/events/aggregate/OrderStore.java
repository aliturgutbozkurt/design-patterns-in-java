package io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;

/**
 * In-memory stand-in for a database: {@link #saveAll} is all-or-nothing (the new state replaces the old one in one
 * assignment), and it can be told to fail the next save, like a lost connection.
 *
 * @see "m11 lesson, section Domain events"
 */
public final class OrderStore {

    /** What is stored per order: state only, never the pending events. */
    private record Row(OrderStatus status, long totalCents) {}

    private SequencedMap<String, Row> rows = new LinkedHashMap<>();
    private boolean failNextSave;

    /** Makes the next {@link #saveAll} throw before anything is written. */
    public void failNextSave() {
        failNextSave = true;
    }

    /** Stores every order, or none of them. */
    public void saveAll(List<Order> orders) {
        if (failNextSave) {
            failNextSave = false;
            throw new IllegalStateException("store unavailable");
        }
        SequencedMap<String, Row> next = new LinkedHashMap<>(rows);
        for (Order order : orders) {
            next.put(order.id(), new Row(order.status(), order.totalCents()));
        }
        rows = next; // the "commit": one assignment
    }

    public Optional<Order> load(String id) {
        Row row = rows.get(Objects.requireNonNull(id, "id"));
        return row == null ? Optional.empty() : Optional.of(Order.restore(id, row.status(), row.totalCents()));
    }

    /** Stored status per order id, in first-save order (an unmodifiable copy). */
    public SequencedMap<String, OrderStatus> statuses() {
        SequencedMap<String, OrderStatus> statuses = new LinkedHashMap<>();
        rows.forEach((id, row) -> statuses.put(id, row.status()));
        return Collections.unmodifiableSequencedMap(statuses);
    }
}
