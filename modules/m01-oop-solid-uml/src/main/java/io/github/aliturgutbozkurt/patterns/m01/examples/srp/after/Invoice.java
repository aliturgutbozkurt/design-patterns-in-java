package io.github.aliturgutbozkurt.patterns.m01.examples.srp.after;

import java.util.List;
import java.util.Objects;

/**
 * An invoice as an immutable value: number, customer and at least one line.
 *
 * @see "m01 lesson, section SRP"
 */
public record Invoice(String number, String customer, List<InvoiceLine> lines) {

    public Invoice {
        Objects.requireNonNull(number, "number");
        Objects.requireNonNull(customer, "customer");
        lines = List.copyOf(lines);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("an invoice needs at least one line");
        }
    }
}
