package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Step 5: a coupon a customer applies by its code; valid up to and including {@code validUntil}.
 *
 * @param code       the code
 * @param percent    1–100
 * @param validUntil last valid day
 * @see "capstone guide §1 Pattern map — Strategy"
 */
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
}
