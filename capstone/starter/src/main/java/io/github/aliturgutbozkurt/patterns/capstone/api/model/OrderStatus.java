package io.github.aliturgutbozkurt.patterns.capstone.api.model;

/**
 * GIVEN — do not modify. The lifecycle states of an order: {@code PLACED → PAID} at checkout, {@code PAID → SHIPPED}
 * (fulfilment), {@code SHIPPED → DELIVERED}, {@code PAID → CANCELLED}.
 *
 * @see "capstone brief, Business rules — Order lifecycle"
 */
public enum OrderStatus {
    PLACED, PAID, SHIPPED, DELIVERED, CANCELLED
}
