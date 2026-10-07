package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import java.time.Instant;
import java.util.Objects;

/**
 * One status change of an order.
 *
 * @param status the status entered
 * @param at     the clock's instant
 * @param note   payment reference, tracking code, reason or {@code ""}
 * @see "capstone guide §1 Pattern map — State"
 */
public record HistoryEntry(OrderStatus status, Instant at, String note) {

    public HistoryEntry {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(note, "note");
    }
}
