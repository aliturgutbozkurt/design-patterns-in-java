package io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import java.util.Objects;

/**
 * GIVEN — do not modify. An order that could not be shipped in a run.
 *
 * @param order  the order (it stays {@code PAID})
 * @param reason the warehouse exception's message
 * @see "capstone brief §2.2 — Fulfilment (F9)"
 */
public record FulfilmentFailure(OrderId order, String reason) {

    public FulfilmentFailure {
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(reason, "reason");
    }
}
