package io.github.aliturgutbozkurt.patterns.capstone.api.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import java.util.Objects;

/**
 * GIVEN — do not modify. One discount of a quote, e.g. {@code ("10% off BOOKS", 99.99)}.
 *
 * @param label  the label from brief §2.2
 * @param amount the amount subtracted, positive
 * @see "capstone brief §2.2 — Pricing (F4)"
 */
public record Adjustment(String label, Money amount) {

    public Adjustment {
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(amount, "amount");
        if (label.isBlank()) {
            throw new IllegalArgumentException("blank label");
        }
    }
}
