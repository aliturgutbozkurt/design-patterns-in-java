package io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate;

/**
 * Lifecycle states: {@code PLACED → PAID → SHIPPED}, and {@code PLACED | PAID → CANCELLED}.
 *
 * @see "m11 lesson, section Domain events"
 */
public enum OrderStatus {
    PLACED,
    PAID,
    SHIPPED,
    CANCELLED
}
