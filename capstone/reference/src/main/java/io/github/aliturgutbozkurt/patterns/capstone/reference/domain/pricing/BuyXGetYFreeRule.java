package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Step 2: for every complete group of {@code buy + free} units of {@code sku}, {@code free} units are free.
 *
 * @param sku  the product
 * @param buy  units paid per group, ≥ 1
 * @param free free units per group, ≥ 1
 * @see "capstone guide §1 Pattern map — Strategy"
 */
@PatternRole(value = DesignPattern.STRATEGY, role = "concrete strategy")
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

    @Override
    public PriceSheet applyTo(PriceSheet sheet) {
        Money total = Money.ZERO;
        List<PricedLine> lines = new ArrayList<>();
        for (PricedLine line : sheet.lines()) {
            if (line.item().sku().equals(sku)) {
                int freeUnits = line.item().quantity() / (buy + free) * free;
                Money discount = line.item().unitPrice().times(freeUnits).min(line.left());
                total = total.plus(discount);
                lines.add(line.lessFreeUnits(discount));
            } else {
                lines.add(line);
            }
        }
        return sheet.withLines(lines).plus(new Discount(label(), total));
    }
}
