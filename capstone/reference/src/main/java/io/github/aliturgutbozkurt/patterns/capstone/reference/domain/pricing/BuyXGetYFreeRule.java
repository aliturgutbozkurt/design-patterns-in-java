package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * Step 2: for every complete group of {@code buy + free} units of {@code sku}, {@code free} units are free.
 *
 * @param sku  the product
 * @param buy  units paid per group, ≥ 1
 * @param free free units per group, ≥ 1
 * @see "capstone guide §1 Pattern map — Strategy"
 */
public record BuyXGetYFreeRule(Sku sku, int buy, int free) implements PromotionRule {

    public BuyXGetYFreeRule {
        Objects.requireNonNull(sku, "sku");
        if (buy < 1 || free < 1) {
            throw new IllegalArgumentException("buy and free must be positive");
        }
    }

    @Override
    public String label() {
        return "buy " + buy + " get " + free + " free: " + sku.value();
    }
}
