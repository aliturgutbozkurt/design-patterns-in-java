package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import java.util.List;
import java.util.Objects;

/**
 * Input of {@link PlaceOrderUseCase}: parsed, but not yet validated against the catalogue.
 *
 * @param customer who orders
 * @param lines what they order (copied)
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public record PlaceOrderCommand(String customer, List<LineRequest> lines) {

    public PlaceOrderCommand {
        Objects.requireNonNull(customer, "customer");
        lines = List.copyOf(lines);
    }

    /** One requested line: a product and a quantity (checked by the use case). */
    public record LineRequest(Sku sku, int quantity) {
        public LineRequest {
            Objects.requireNonNull(sku, "sku");
        }
    }
}
