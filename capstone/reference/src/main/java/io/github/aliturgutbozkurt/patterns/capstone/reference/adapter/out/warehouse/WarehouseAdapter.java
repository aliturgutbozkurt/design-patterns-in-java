package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.warehouse;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.WarehouseApi;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.WarehouseException;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ShipmentOutcome;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.Warehouse;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderItem;
import java.util.List;
import java.util.Objects;

/**
 * Outbound adapter: drives the warehouse's three blocking calls for one order and turns a
 * {@link WarehouseException} into a {@link ShipmentOutcome.Failed} with its message.
 *
 * @see "capstone guide §1 Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "outbound adapter")
public final class WarehouseAdapter implements Warehouse {

    private final WarehouseApi api;

    public WarehouseAdapter(WarehouseApi api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    @Override
    public ShipmentOutcome ship(OrderId order, List<OrderItem> physicalItems, String postalCode) {
        try {
            for (OrderItem item : physicalItems) {
                api.pick(order.value(), item.sku().value(), item.quantity());
            }
            String parcel = api.pack(order.value());
            return new ShipmentOutcome.Shipped(api.ship(parcel, postalCode));
        } catch (WarehouseException e) {
            return new ShipmentOutcome.Failed(String.valueOf(e.getMessage()));
        }
    }
}
