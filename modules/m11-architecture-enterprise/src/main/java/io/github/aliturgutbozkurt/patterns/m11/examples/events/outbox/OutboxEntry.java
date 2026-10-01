package io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox;

import java.util.Objects;

/**
 * One event waiting in the outbox table, written in the same transaction as the order state.
 *
 * @param sequence position in the outbox (1, 2, …) — also the consumers' deduplication key
 * @param type event type, e.g. {@code OrderPlaced}
 * @param payload serialized event data
 * @param published whether the relay has handed it to the broker
 * @see "m11 lesson, section Domain events — transactional outbox"
 */
public record OutboxEntry(long sequence, String type, String payload, boolean published) {

    public OutboxEntry {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(payload, "payload");
    }

    /** The same entry, marked as published. */
    public OutboxEntry asPublished() {
        return new OutboxEntry(sequence, type, payload, true);
    }

    @Override
    public String toString() {
        return sequence + " " + type + " " + payload;
    }
}
