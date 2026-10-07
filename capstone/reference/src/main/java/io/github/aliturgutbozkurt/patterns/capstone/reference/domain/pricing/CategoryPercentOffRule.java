package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Step 3: {@code percent}% of what is left of each line of {@code category} after step 2, rounded half-even per line.
 *
 * @param category the category
 * @param percent  1–100
 * @see "capstone guide, Pattern map — Strategy"
 */
@PatternRole(value = DesignPattern.STRATEGY, role = "concrete strategy")
public record CategoryPercentOffRule(Category category, int percent) implements PromotionRule {

    public CategoryPercentOffRule {
        Objects.requireNonNull(category, "category");
        if (percent < 1 || percent > 100) {
            throw new IllegalArgumentException("percent out of range: " + percent);
        }
    }

    @Override
    public String label() {
        return percent + "% off " + category.name();
    }

    @Override
    public PriceSheet applyTo(PriceSheet sheet) {
        Money total = Money.ZERO;
        List<PricedLine> lines = new ArrayList<>();
        for (PricedLine line : sheet.lines()) {
            if (line.item().category() == category) {
                Money discount = line.afterFreeUnits().percent(percent).min(line.left());
                total = total.plus(discount);
                lines.add(line.less(discount));
            } else {
                lines.add(line);
            }
        }
        return sheet.withLines(lines).plus(new Discount(label(), total));
    }
}
