package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.List;

/**
 * Step 5: the cart's coupon, if it is still valid today; an expired coupon gives nothing.
 *
 * @see "capstone guide, Pattern map — Decorator"
 */
@PatternRole(value = DesignPattern.DECORATOR, role = "concrete decorator")
public final class CouponDiscount extends PriceStepDecorator {

    private final List<PromotionRule> rules;

    public CouponDiscount(PriceStep inner, List<PromotionRule> rules) {
        super(inner);
        this.rules = List.copyOf(rules);
    }

    @Override
    protected PriceSheet adjust(PriceSheet sheet, Basket basket) {
        for (PromotionRule rule : rules) {
            if (rule instanceof CouponRule coupon && coupon.code().equals(basket.coupon())
                    && coupon.isValidOn(basket.today())) {
                return coupon.applyTo(sheet);
            }
        }
        return sheet;
    }
}
