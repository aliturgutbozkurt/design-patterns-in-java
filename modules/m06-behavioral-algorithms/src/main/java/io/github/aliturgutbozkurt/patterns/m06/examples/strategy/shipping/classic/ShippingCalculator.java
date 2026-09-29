package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * The Strategy context: it holds a strategy, delegates every quote to it and can switch it at run time.
 *
 * @see "m06 lesson, section Strategy"
 */
public final class ShippingCalculator {

    private ShippingStrategy strategy;

    public ShippingCalculator(ShippingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public void setStrategy(ShippingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public ShippingStrategy strategy() {
        return strategy;
    }

    public BigDecimal quote(Parcel parcel) {
        return strategy.cost(parcel);
    }
}
