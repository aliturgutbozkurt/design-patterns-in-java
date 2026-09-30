package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.memory;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.OrderIds;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;

/**
 * Outbound adapter: {@code order-1}, {@code order-2}, … continuing after {@code alreadyUsed} ids.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class SequentialOrderIds implements OrderIds {

    private long last;

    public SequentialOrderIds(long alreadyUsed) {
        if (alreadyUsed < 0) {
            throw new IllegalArgumentException("alreadyUsed must not be negative: " + alreadyUsed);
        }
        this.last = alreadyUsed;
    }

    @Override
    public OrderId next() {
        return new OrderId("order-" + ++last);
    }
}
