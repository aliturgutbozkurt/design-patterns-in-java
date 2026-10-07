package io.github.aliturgutbozkurt.patterns.capstone.api.order;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import java.time.Instant;
import java.util.Objects;

/**
 * GIVEN — do not modify. One entry of an order's history.
 *
 * @param status the status entered
 * @param at     the clock's instant
 * @param note   see {@link OrderView#history()}
 * @see "capstone brief §2.2 — Order lifecycle (F7)"
 */
public record StatusChange(OrderStatus status, Instant at, String note) {

    public StatusChange {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(note, "note");
    }
}
