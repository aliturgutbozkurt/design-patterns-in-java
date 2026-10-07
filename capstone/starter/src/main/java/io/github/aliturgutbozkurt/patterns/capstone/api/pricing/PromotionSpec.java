package io.github.aliturgutbozkurt.patterns.capstone.api.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.time.LocalDate;
import java.util.Objects;

/**
 * GIVEN — do not modify. The four kinds of promotion, as data. How each is priced is the shop's business (brief §2.2,
 * steps 2–5).
 *
 * @see "capstone brief §2.2 — Pricing (F4)"
 */
public sealed interface PromotionSpec {

    /**
     * Step 2: for every complete group of {@code buy + free} units of {@code sku}, {@code free} units are free.
     * Label: {@code buy 2 get 1 free: TOY-001}.
     *
     * @param sku  the product
     * @param buy  units to pay for per group, at least 1
     * @param free free units per group, at least 1
     */
    record BuyXGetYFree(Sku sku, int buy, int free) implements PromotionSpec {
        public BuyXGetYFree {
            Objects.requireNonNull(sku, "sku");
            if (buy < 1 || free < 1) {
                throw new IllegalArgumentException("buy and free must be positive: " + buy + ", " + free);
            }
        }
    }

    /**
     * Step 3: {@code percent}% off every line of {@code category}. Label: {@code 10% off BOOKS}.
     *
     * @param category the category
     * @param percent  1–100
     */
    record CategoryPercentOff(Category category, int percent) implements PromotionSpec {
        public CategoryPercentOff {
            Objects.requireNonNull(category, "category");
            if (percent < 1 || percent > 100) {
                throw new IllegalArgumentException("percent out of range: " + percent);
            }
        }
    }

    /**
     * Step 4: {@code off} when the discounted subtotal is at least {@code threshold}. Label:
     * {@code 100.00 off over 1000.00}.
     *
     * @param threshold the minimum discounted subtotal
     * @param off       the amount subtracted, positive
     */
    record AmountOffOver(Money threshold, Money off) implements PromotionSpec {
        public AmountOffOver {
            Objects.requireNonNull(threshold, "threshold");
            Objects.requireNonNull(off, "off");
            if (off.isZero()) {
                throw new IllegalArgumentException("amount off must be positive");
            }
        }
    }

    /**
     * Step 5: a coupon a customer applies to a cart by its code. Valid up to and including {@code validUntil} (in the
     * clock's time zone). Label: {@code coupon AUTUMN5 5%}.
     *
     * @param code       the code customers type, non-blank, without spaces
     * @param percent    1–100
     * @param validUntil last day the coupon is valid
     */
    record Coupon(String code, int percent, LocalDate validUntil) implements PromotionSpec {
        public Coupon {
            Objects.requireNonNull(code, "code");
            Objects.requireNonNull(validUntil, "validUntil");
            if (code.isBlank() || code.chars().anyMatch(Character::isWhitespace)) {
                throw new IllegalArgumentException("malformed coupon code: '" + code + "'");
            }
            if (percent < 1 || percent > 100) {
                throw new IllegalArgumentException("percent out of range: " + percent);
            }
        }
    }
}
