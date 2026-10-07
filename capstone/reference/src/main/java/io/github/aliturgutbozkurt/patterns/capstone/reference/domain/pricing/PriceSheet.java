package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The immutable state that flows through the pricing pipeline; every step returns a new sheet.
 *
 * @param lines     the priced lines in cart order
 * @param subtotal  sum of the line totals
 * @param discounts the discounts so far, in step order (none of 0.00)
 * @param shipping  the shipping fee
 * @see "capstone guide, Slice walkthrough — C4"
 */
public record PriceSheet(List<PricedLine> lines, Money subtotal, List<Discount> discounts, Money shipping) {

    public PriceSheet {
        lines = List.copyOf(lines);
        Objects.requireNonNull(subtotal, "subtotal");
        discounts = List.copyOf(discounts);
        Objects.requireNonNull(shipping, "shipping");
    }

    /** Step 1: the lines and their subtotal, no discounts, no shipping. */
    public static PriceSheet of(List<PricedLine> lines) {
        Money subtotal = lines.stream().map(PricedLine::lineTotal).reduce(Money.ZERO, Money::plus);
        return new PriceSheet(lines, subtotal, List.of(), Money.ZERO);
    }

    /** Subtotal minus the discounts — never negative, because every discount is capped at what is left. */
    public Money merchandise() {
        return discounts.stream().map(Discount::amount).reduce(subtotal, Money::minus);
    }

    /** Merchandise plus shipping. */
    public Money total() {
        return merchandise().plus(shipping);
    }

    /** Whether any line must be shipped. */
    public boolean hasPhysicalItems() {
        return lines.stream().anyMatch(line -> line.item().type() == ProductType.PHYSICAL);
    }

    /** This sheet with changed lines (after line discounts). */
    public PriceSheet withLines(List<PricedLine> changed) {
        return new PriceSheet(changed, subtotal, discounts, shipping);
    }

    /** This sheet with one more discount; a discount of 0.00 is not listed. */
    public PriceSheet plus(Discount discount) {
        if (discount.amount().isZero()) {
            return this;
        }
        List<Discount> more = new ArrayList<>(discounts);
        more.add(discount);
        return new PriceSheet(lines, subtotal, more, shipping);
    }

    /** This sheet with a shipping fee. */
    public PriceSheet withShipping(Money fee) {
        return new PriceSheet(lines, subtotal, discounts, fee);
    }
}
