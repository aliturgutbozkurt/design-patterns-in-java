package io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Handler: reserves stock once an order is paid. Adding it needed no change to {@link Order}.
 *
 * @see "m11 lesson, section Domain events"
 */
public final class ReserveStock implements Consumer<OrderEvent.OrderPaid> {

    private final Consumer<String> warehouse;

    public ReserveStock(Consumer<String> warehouse) {
        this.warehouse = Objects.requireNonNull(warehouse, "warehouse");
    }

    @Override
    public void accept(OrderEvent.OrderPaid paid) {
        warehouse.accept("stock: reserved for " + paid.orderId());
    }
}
