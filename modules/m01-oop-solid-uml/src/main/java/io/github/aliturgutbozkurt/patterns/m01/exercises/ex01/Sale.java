package io.github.aliturgutbozkurt.patterns.m01.exercises.ex01;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** GIVEN — do not modify. One sale: region, product and amount (scale 2). */
public record Sale(String region, String product, BigDecimal amount) {

    public Sale {
        Objects.requireNonNull(region, "region");
        Objects.requireNonNull(product, "product");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + amount);
        }
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }
}
