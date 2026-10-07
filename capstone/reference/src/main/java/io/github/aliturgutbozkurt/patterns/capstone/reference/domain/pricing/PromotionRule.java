package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

/**
 * A promotion the shop runs, as the domain sees it (brief §2.2 "Pricing", steps 2–5).
 *
 * @see "capstone guide §1 Pattern map — Strategy"
 */
public sealed interface PromotionRule permits BuyXGetYFreeRule, CategoryPercentOffRule, AmountOffOverRule, CouponRule {

    /** The label of the discount this rule gives, e.g. {@code 10% off BOOKS}. */
    String label();
}
