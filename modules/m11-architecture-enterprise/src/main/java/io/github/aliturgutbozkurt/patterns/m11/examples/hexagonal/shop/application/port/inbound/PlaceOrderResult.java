package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import java.util.Objects;

/**
 * Business outcomes of placing an order. Infrastructure failures (a broken disk) stay exceptions; a declined card is
 * an expected outcome, so it is a value the caller must handle.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public sealed interface PlaceOrderResult {

    /** The order was charged, saved and announced. */
    record Placed(OrderId orderId, Money total) implements PlaceOrderResult {}

    /** Nothing was saved or published; {@code reason} says why. */
    record Rejected(String reason) implements PlaceOrderResult {
        public Rejected {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
