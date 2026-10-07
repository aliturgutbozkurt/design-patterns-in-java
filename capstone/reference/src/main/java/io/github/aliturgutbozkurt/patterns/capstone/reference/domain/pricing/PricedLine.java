package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import java.util.Objects;

/**
 * A line during pricing: its total, what is left after the free units of step 2, and what is left now.
 *
 * @param item           the basket line
 * @param lineTotal      quantity × unit price (step 1)
 * @param afterFreeUnits what is left after step 2 — the base of category percentages (step 3)
 * @param left           what is left after every line discount so far
 * @see "capstone guide §2 Slice walkthrough — C4"
 */
public record PricedLine(BasketLine item, Money lineTotal, Money afterFreeUnits, Money left) {

    public PricedLine {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(lineTotal, "lineTotal");
        Objects.requireNonNull(afterFreeUnits, "afterFreeUnits");
        Objects.requireNonNull(left, "left");
    }

    /** Step 1 for one line. */
    public static PricedLine of(BasketLine item) {
        Money total = item.unitPrice().times(item.quantity());
        return new PricedLine(item, total, total, total);
    }

    /** This line after a step-2 discount (free units). */
    public PricedLine lessFreeUnits(Money discount) {
        return new PricedLine(item, lineTotal, afterFreeUnits.minus(discount), left.minus(discount));
    }

    /** This line after a step-3 discount. */
    public PricedLine less(Money discount) {
        return new PricedLine(item, lineTotal, afterFreeUnits, left.minus(discount));
    }
}
