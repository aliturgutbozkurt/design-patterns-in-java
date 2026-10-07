package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;

/**
 * Step 1: line totals and subtotal — the innermost stage.
 *
 * @see "capstone guide §1 Pattern map — Decorator"
 */
@PatternRole(value = DesignPattern.DECORATOR, role = "concrete component")
public final class BasePrices implements PriceStep {

    @Override
    public PriceSheet price(Basket basket) {
        return PriceSheet.of(basket.lines().stream().map(PricedLine::of).toList());
    }
}
