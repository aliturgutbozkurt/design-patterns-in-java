package io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderItem;
import java.util.List;

/**
 * Outbound port: has the warehouse pick, pack and ship one order. Blocking; safe to call from many threads at once.
 *
 * @see "capstone guide §1 Pattern map — Thread-per-task"
 */
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "outbound port")
@FunctionalInterface
public interface Warehouse {

    /** Picks every line in order, packs them and ships the parcel to {@code postalCode}. */
    ShipmentOutcome ship(OrderId order, List<OrderItem> physicalItems, String postalCode);
}
