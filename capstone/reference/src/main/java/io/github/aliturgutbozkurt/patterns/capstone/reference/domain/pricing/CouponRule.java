package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Step 5: {@code percent}% of what is left after step 4, rounded half-up once; valid up to and including
 * {@code validUntil}.
 *
 * @param code       the code
 * @param percent    1–100
 * @param validUntil last valid day
 * @see "capstone guide §1 Pattern map — Strategy"
 */
@PatternRole(value = DesignPattern.STRATEGY, role = "concrete strategy")
public record CouponRule(String code, int percent, LocalDate validUntil) implements PromotionRule {

    public CouponRule {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(validUntil, "validUntil");
        if (percent < 1 || percent > 100) {
            throw new IllegalArgumentException("percent out of range: " + percent);
        }
    }

    /** Whether the coupon may be used on {@code day}. */
    public boolean isValidOn(LocalDate day) {
        return !day.isAfter(validUntil);
    }

    @Override
    public String label() {
        return "coupon " + code + " " + percent + "%";
    }

    @Override
    public PriceSheet applyTo(PriceSheet sheet) {
        return sheet.plus(new Discount(label(), sheet.merchandise().percent(percent)));
    }
}
