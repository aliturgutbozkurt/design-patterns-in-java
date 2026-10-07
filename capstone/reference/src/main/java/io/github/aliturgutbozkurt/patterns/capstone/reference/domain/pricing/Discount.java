package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import java.util.Objects;

/**
 * One applied discount.
 *
 * @param label  e.g. {@code 10% off BOOKS}
 * @param amount what it took off
 * @see "capstone guide §2 Slice walkthrough — C4"
 */
public record Discount(String label, Money amount) {

    public Discount {
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(amount, "amount");
    }
}
