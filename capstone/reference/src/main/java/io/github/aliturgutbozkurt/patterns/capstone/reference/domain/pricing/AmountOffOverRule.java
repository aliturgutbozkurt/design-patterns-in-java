package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.Objects;

/**
 * Step 4: {@code off} when what is left after steps 2–3 is at least {@code threshold}.
 *
 * @param threshold the minimum discounted subtotal
 * @param off       the amount subtracted (capped at what is left)
 * @see "capstone guide, Pattern map — Strategy"
 */
@PatternRole(value = DesignPattern.STRATEGY, role = "concrete strategy")
public record AmountOffOverRule(Money threshold, Money off) implements PromotionRule {

    public AmountOffOverRule {
        Objects.requireNonNull(threshold, "threshold");
        Objects.requireNonNull(off, "off");
    }

    @Override
    public String label() {
        return off.toPlainString() + " off over " + threshold.toPlainString();
    }

    /** Whether the sheet's merchandise total reaches the threshold. */
    public boolean qualifies(PriceSheet sheet) {
        return sheet.merchandise().compareTo(threshold) >= 0;
    }

    @Override
    public PriceSheet applyTo(PriceSheet sheet) {
        return qualifies(sheet) ? sheet.plus(new Discount(label(), off.min(sheet.merchandise()))) : sheet;
    }
}
