package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.List;

/**
 * Steps 2 and 3: every buy-X-get-Y-free rule, then every category rule, in registration order.
 *
 * @see "capstone guide §1 Pattern map — Decorator"
 */
@PatternRole(value = DesignPattern.DECORATOR, role = "concrete decorator")
public final class LinePromotions extends PriceStepDecorator {

    private final List<PromotionRule> rules;

    public LinePromotions(PriceStep inner, List<PromotionRule> rules) {
        super(inner);
        this.rules = List.copyOf(rules);
    }

    @Override
    protected PriceSheet adjust(PriceSheet sheet, Basket basket) {
        PriceSheet result = sheet;
        for (PromotionRule rule : rules) {
            if (rule instanceof BuyXGetYFreeRule freeUnits) {
                result = freeUnits.applyTo(result);
            }
        }
        for (PromotionRule rule : rules) {
            if (rule instanceof CategoryPercentOffRule percentOff) {
                result = percentOff.applyTo(result);
            }
        }
        return result;
    }
}
