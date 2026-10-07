package io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.CouponRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PromotionRule;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port: the promotions the shop runs. Implementations are thread-safe.
 *
 * @see "capstone guide, Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.REPOSITORY, role = "repository (outbound port)")
public interface PromotionRepository {

    /** Adds a promotion after the existing ones. */
    void add(PromotionRule rule);

    /** Every promotion in registration order. */
    List<PromotionRule> all();

    /** The coupon with this code, if any. */
    Optional<CouponRule> coupon(String code);
}
