package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import java.util.Objects;

/**
 * Step 4: {@code off} when what is left after steps 2–3 is at least {@code threshold}.
 *
 * @param threshold the minimum discounted subtotal
 * @param off       the amount subtracted (capped at what is left)
 * @see "capstone guide §1 Pattern map — Strategy"
 */
public record AmountOffOverRule(Money threshold, Money off) implements PromotionRule {

    public AmountOffOverRule {
        Objects.requireNonNull(threshold, "threshold");
        Objects.requireNonNull(off, "off");
    }

    @Override
    public String label() {
        return off.toPlainString() + " off over " + threshold.toPlainString();
    }
}
