package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import java.util.Objects;

/**
 * Step 3: {@code percent}% off every line of {@code category}, per line, rounded half-up.
 *
 * @param category the category
 * @param percent  1–100
 * @see "capstone guide §1 Pattern map — Strategy"
 */
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
}
