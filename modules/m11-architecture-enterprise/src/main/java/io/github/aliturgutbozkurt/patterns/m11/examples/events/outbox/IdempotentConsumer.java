package io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Consumer side of at-least-once delivery: remembers which sequences it has processed and ignores re-deliveries.
 *
 * @see "m11 lesson, section Domain events — transactional outbox"
 */
public final class IdempotentConsumer {

    private final Consumer<OutboxEntry> handler;
    private final Set<Long> processed = new HashSet<>();
    private int duplicates;

    public IdempotentConsumer(Consumer<OutboxEntry> handler) {
        this.handler = Objects.requireNonNull(handler, "handler");
    }

    /** Processes {@code entry} unless its sequence was seen before; returns whether it was processed. */
    public boolean receive(OutboxEntry entry) {
        if (!processed.add(entry.sequence())) {
            duplicates++;
            return false;
        }
        handler.accept(entry);
        return true;
    }

    /** How many re-deliveries were ignored. */
    public int duplicates() {
        return duplicates;
    }
}
