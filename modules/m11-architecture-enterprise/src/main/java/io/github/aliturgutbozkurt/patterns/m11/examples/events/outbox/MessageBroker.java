package io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox;

/**
 * Outbound port to a message broker (Kafka, JMS, … in real life); may throw when the broker is unreachable.
 *
 * @see "m11 lesson, section Domain events — transactional outbox"
 */
@FunctionalInterface
public interface MessageBroker {

    void send(OutboxEntry entry);
}
