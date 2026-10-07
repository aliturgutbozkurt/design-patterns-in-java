package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;

/**
 * A promotion kind as an interchangeable pricing algorithm: every kind applies itself to a price sheet, so the
 * pipeline never needs to know how a kind computes its discount.
 *
 * @see "capstone guide, Pattern map — Strategy"
 */
@PatternRole(value = DesignPattern.STRATEGY, role = "strategy (sealed interface of records)")
public sealed interface PromotionRule permits BuyXGetYFreeRule, CategoryPercentOffRule, AmountOffOverRule, CouponRule {

    /** The label of the discount this rule gives, e.g. {@code 10% off BOOKS}. */
    String label();

    /** The sheet after this promotion; the discount is capped at what is left and omitted when 0.00. */
    PriceSheet applyTo(PriceSheet sheet);
}
