package io.github.aliturgutbozkurt.patterns.m00.exercises.ex02;

import java.math.BigDecimal;

/** GIVEN — do not modify. A card payment; international cards cost extra. */
public record CardPayment(BigDecimal amount, boolean international) implements Payment {

    public CardPayment {
        Payment.requirePositive(amount);
    }
}
