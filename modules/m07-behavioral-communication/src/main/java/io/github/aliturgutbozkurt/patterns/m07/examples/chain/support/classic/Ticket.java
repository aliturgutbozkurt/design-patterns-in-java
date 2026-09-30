package io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic;

import java.util.Objects;

/**
 * A support request travelling along the chain; severity 1 (minor) to 5 (critical).
 *
 * @see "m07 lesson, section Chain of Responsibility"
 */
public record Ticket(String id, Topic topic, int severity) {

    public Ticket {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(topic, "topic");
        if (severity < 1 || severity > 5) {
            throw new IllegalArgumentException("severity must be 1..5: " + severity);
        }
    }
}
