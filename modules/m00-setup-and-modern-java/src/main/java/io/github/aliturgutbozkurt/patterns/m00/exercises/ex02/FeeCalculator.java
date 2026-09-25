package io.github.aliturgutbozkurt.patterns.m00.exercises.ex02;

import java.math.BigDecimal;

/** GIVEN — do not modify. Computes the processing fee for a payment. */
public interface FeeCalculator {

    /**
     * @return the fee in euros, scale 2, rounded {@code HALF_EVEN}
     * @throws NullPointerException if {@code payment} is {@code null}
     */
    BigDecimal feeFor(Payment payment);
}
