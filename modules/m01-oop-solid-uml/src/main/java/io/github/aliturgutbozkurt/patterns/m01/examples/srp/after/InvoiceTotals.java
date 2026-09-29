package io.github.aliturgutbozkurt.patterns.m01.examples.srp.after;

import java.math.BigDecimal;

/**
 * The result of a calculation: subtotal, VAT and total, all scale 2.
 *
 * @see "m01 lesson, section SRP"
 */
public record InvoiceTotals(BigDecimal subtotal, BigDecimal vat, BigDecimal total) {}
