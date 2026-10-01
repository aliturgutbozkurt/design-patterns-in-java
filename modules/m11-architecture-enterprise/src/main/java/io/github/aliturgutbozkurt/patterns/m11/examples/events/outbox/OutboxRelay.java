package io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox;

import java.util.Objects;

/**
 * Relay: sends pending outbox entries to the broker in sequence order and marks each one published afterwards. If
 * the broker fails, the relay stops (order preserved) and the next run resumes at the failed entry. A crash between
 * "sent" and "marked" re-sends that entry: delivery is <em>at least once</em>.
 *
 * @see "m11 lesson, section Domain events — transactional outbox"
 */
public final class OutboxRelay {

    private final OutboxOrderStore store;

    public OutboxRelay(OutboxOrderStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    /**
     * Relays every pending entry and returns how many were published.
     *
     * @throws RuntimeException whatever the broker threw; the failed entry and all later ones stay pending
     */
    public int relayPending(MessageBroker broker) {
        Objects.requireNonNull(broker, "broker");
        int published = 0;
        for (OutboxEntry entry : store.pending()) {
            broker.send(entry);
            store.markPublished(entry.sequence());
            published++;
        }
        return published;
    }
}
