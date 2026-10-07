package io.github.aliturgutbozkurt.patterns.capstone.api.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import java.util.List;
import java.util.Objects;

/**
 * GIVEN — do not modify. The price of a cart. Invariant: {@code total = subtotal − sum(discounts) + shipping}; a
 * discount never exceeds what is left to discount, and discounts of 0.00 are not listed.
 *
 * @param lines     one line per cart line, in cart order
 * @param subtotal  sum of the line totals (step 1)
 * @param discounts the applied discounts in step order (steps 2–5)
 * @param shipping  shipping fee (step 7)
 * @param total     what the customer pays
 * @see "capstone brief, Business rules — Pricing (F4)"
 */
public record PriceQuote(List<QuoteLine> lines, Money subtotal, List<Adjustment> discounts, Money shipping,
                         Money total) {

    public PriceQuote {
        lines = List.copyOf(lines);
        Objects.requireNonNull(subtotal, "subtotal");
        discounts = List.copyOf(discounts);
        Objects.requireNonNull(shipping, "shipping");
        Objects.requireNonNull(total, "total");
    }
}
