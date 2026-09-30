package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.modern;

import io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic.Parcel;
import java.math.BigDecimal;

/**
 * Strategies as enum constants: a closed, named set that can be stored in a database or chosen in a form by name.
 *
 * @see "m06 lesson, section Strategy"
 */
public enum ShippingOption implements ShippingRule {
    FLAT(ShippingRules.flatRate(new BigDecimal("4.99"))),
    PER_KG(ShippingRules.weightBased(new BigDecimal("2.00"), new BigDecimal("0.80"))),
    FREE_OVER_50(ShippingRules.freeOver(new BigDecimal("50.00"), FLAT));

    private final ShippingRule rule;

    ShippingOption(ShippingRule rule) {
        this.rule = rule;
    }

    @Override
    public BigDecimal cost(Parcel parcel) {
        return rule.cost(parcel);
    }
}
