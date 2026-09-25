package io.github.aliturgutbozkurt.patterns.m00.exercises.ex02;

import java.math.BigDecimal;
import java.util.Objects;

/** GIVEN — do not modify. A bank transfer to or from an IBAN. */
public record BankTransfer(BigDecimal amount, String iban) implements Payment {

    public BankTransfer {
        Payment.requirePositive(amount);
        Objects.requireNonNull(iban, "iban");
    }
}
