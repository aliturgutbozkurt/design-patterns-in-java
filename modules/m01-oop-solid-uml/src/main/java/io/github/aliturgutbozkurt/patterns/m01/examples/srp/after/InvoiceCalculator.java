package io.github.aliturgutbozkurt.patterns.m01.examples.srp.after;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Responsibility: the money rules. Changes only when tax or rounding rules change.
 *
 * @see "m01 lesson, section SRP"
 */
public final class InvoiceCalculator {

    /** 20 % VAT (Turkish KDV). */
    public static final BigDecimal VAT_RATE = new BigDecimal("0.20");

    public InvoiceTotals totals(Invoice invoice) {
        BigDecimal subtotal = invoice.lines().stream()
                .map(InvoiceLine::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal vat = subtotal.multiply(VAT_RATE).setScale(2, RoundingMode.HALF_EVEN);
        return new InvoiceTotals(subtotal, vat, subtotal.add(vat));
    }
}
