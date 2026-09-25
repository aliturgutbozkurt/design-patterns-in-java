package io.github.aliturgutbozkurt.patterns.m00.exercises.ex02;

import java.math.BigDecimal;

/** Assignment 02 — your fee calculator. See assignments/02-payment-fees.en.md (Türkçe: 02-payment-fees.tr.md). */
public class PaymentFees implements FeeCalculator {

    @Override
    public BigDecimal feeFor(Payment payment) {
        // TODO(ex02): one switch over the sealed Payment, with record patterns and guards, and no default branch.
        throw new UnsupportedOperationException("TODO(ex02): implement feeFor(Payment)");
    }
}
