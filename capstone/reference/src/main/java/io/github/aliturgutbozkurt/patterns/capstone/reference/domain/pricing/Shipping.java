package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;

/**
 * Step 7: 49.90 when something must be shipped and the merchandise total is below 500.00 — the outermost stage.
 *
 * @see "capstone guide, Pattern map — Decorator"
 */
@PatternRole(value = DesignPattern.DECORATOR, role = "concrete decorator")
public final class Shipping extends PriceStepDecorator {

    /** The fee below the free-shipping threshold. */
    public static final Money FEE = Money.of("49.90");
    /** From this merchandise total on, shipping is free. */
    public static final Money FREE_FROM = Money.of("500.00");

    public Shipping(PriceStep inner) {
        super(inner);
    }

    @Override
    protected PriceSheet adjust(PriceSheet sheet, Basket basket) {
        boolean charged = sheet.hasPhysicalItems() && sheet.merchandise().compareTo(FREE_FROM) < 0;
        return sheet.withShipping(charged ? FEE : Money.ZERO);
    }
}
