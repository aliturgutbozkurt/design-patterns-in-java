package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.Comparator;
import java.util.List;

/**
 * Step 4: of the amount-off rules whose threshold is reached, only the one with the highest threshold applies.
 *
 * @see "capstone guide, Pattern map — Decorator"
 */
@PatternRole(value = DesignPattern.DECORATOR, role = "concrete decorator")
public final class OrderPromotion extends PriceStepDecorator {

    private final List<PromotionRule> rules;

    public OrderPromotion(PriceStep inner, List<PromotionRule> rules) {
        super(inner);
        this.rules = List.copyOf(rules);
    }

    @Override
    protected PriceSheet adjust(PriceSheet sheet, Basket basket) {
        return rules.stream()
                .filter(AmountOffOverRule.class::isInstance)
                .map(AmountOffOverRule.class::cast)
                .filter(rule -> rule.qualifies(sheet))
                .max(Comparator.comparing(AmountOffOverRule::threshold))
                .map(rule -> rule.applyTo(sheet))
                .orElse(sheet);
    }
}
