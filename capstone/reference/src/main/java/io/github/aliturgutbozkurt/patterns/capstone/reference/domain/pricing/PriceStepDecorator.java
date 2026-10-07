package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.Objects;

/**
 * A stage that wraps another stage: it lets the inner stage price the basket first, then adjusts the result.
 *
 * @see "capstone guide, Pattern map — Decorator"
 */
@PatternRole(value = DesignPattern.DECORATOR, role = "decorator")
public abstract class PriceStepDecorator implements PriceStep {

    private final PriceStep inner;

    protected PriceStepDecorator(PriceStep inner) {
        this.inner = Objects.requireNonNull(inner, "inner");
    }

    @Override
    public final PriceSheet price(Basket basket) {
        return adjust(inner.price(basket), basket);
    }

    /** This stage's change to the sheet the inner stages produced. */
    protected abstract PriceSheet adjust(PriceSheet sheet, Basket basket);
}
