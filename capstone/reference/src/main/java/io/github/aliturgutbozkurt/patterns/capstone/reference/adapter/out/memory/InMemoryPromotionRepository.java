package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PromotionRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.CouponRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PromotionRule;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Promotions in a copy-on-write list: added rarely, read on every quote.
 *
 * @see "capstone guide, Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.REPOSITORY, role = "in-memory repository (outbound adapter)")
public final class InMemoryPromotionRepository implements PromotionRepository {

    private final List<PromotionRule> rules = new CopyOnWriteArrayList<>();

    @Override
    public void add(PromotionRule rule) {
        rules.add(Objects.requireNonNull(rule, "rule"));
    }

    @Override
    public List<PromotionRule> all() {
        return List.copyOf(rules);
    }

    @Override
    public Optional<CouponRule> coupon(String code) {
        for (PromotionRule rule : rules) {
            if (rule instanceof CouponRule coupon && coupon.code().equals(code)) {
                return Optional.of(coupon);
            }
        }
        return Optional.empty();
    }
}
