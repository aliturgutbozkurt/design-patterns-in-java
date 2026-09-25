package io.github.aliturgutbozkurt.patterns.m00.exercises.ex02;

import java.math.BigDecimal;

/** GIVEN — do not modify. The ways a customer can pay. The set is closed: exactly three kinds. */
public sealed interface Payment permits CardPayment, BankTransfer, WalletPayment {

    /** The amount paid, in euros; always positive. */
    BigDecimal amount();

    /** Shared validation used by the permitted records. */
    static BigDecimal requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        return amount;
    }
}
