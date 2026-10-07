package io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import java.util.List;

/**
 * GIVEN — do not modify. The outcome of one fulfilment run; both lists are in order-number order.
 *
 * @param shipped the orders now {@code SHIPPED}
 * @param failed  the orders that stay {@code PAID}, with the reason
 * @see "capstone brief, Business rules — Fulfilment (F9)"
 */
public record FulfilmentReport(List<OrderId> shipped, List<FulfilmentFailure> failed) {

    public FulfilmentReport {
        shipped = List.copyOf(shipped);
        failed = List.copyOf(failed);
    }
}
