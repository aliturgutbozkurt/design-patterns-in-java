package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import java.util.List;

/**
 * Builds the pricing stages in the fixed order of brief §2.2 by wrapping them: base prices → line promotions → order
 * promotion → coupon → shipping.
 *
 * @see "capstone guide §1 Pattern map — Decorator"
 */
public final class PricingPipeline {

    private PricingPipeline() {
    }

    /** The standard pipeline for the given promotions. */
    public static PriceStep standard(List<PromotionRule> rules) {
        return new Shipping(new CouponDiscount(new OrderPromotion(new LinePromotions(new BasePrices(), rules), rules),
                rules));
    }
}
