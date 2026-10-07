package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PriceQuote;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PricingUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec.AmountOffOver;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec.BuyXGetYFree;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec.CategoryPercentOff;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec.Coupon;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PromotionRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.AmountOffOverRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.BuyXGetYFreeRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.CategoryPercentOffRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.CouponRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PromotionRule;
import java.util.Objects;

/**
 * Feature F4: registers promotions and prices carts.
 *
 * @see "capstone guide §2 Slice walkthrough — C4"
 */
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "application service behind an inbound port")
public final class PricingService implements PricingUseCase {

    private final PromotionRepository promotions;

    public PricingService(PromotionRepository promotions) {
        this.promotions = Objects.requireNonNull(promotions, "promotions");
    }

    @Override
    public void addPromotion(PromotionSpec promotion) {
        promotions.add(toRule(promotion));
    }

    @Override
    public PriceQuote quote(CartId cart) {
        throw new UnsupportedOperationException("the pricing pipeline arrives in C4");
    }

    /** The domain rule for a GIVEN promotion spec (exhaustive over the sealed spec, no default). */
    static PromotionRule toRule(PromotionSpec promotion) {
        return switch (promotion) {
            case BuyXGetYFree(var sku, var buy, var free) -> new BuyXGetYFreeRule(sku, buy, free);
            case CategoryPercentOff(var category, var percent) -> new CategoryPercentOffRule(category, percent);
            case AmountOffOver(var threshold, var off) -> new AmountOffOverRule(threshold, off);
            case Coupon(var code, var percent, var validUntil) -> new CouponRule(code, percent, validUntil);
        };
    }
}
