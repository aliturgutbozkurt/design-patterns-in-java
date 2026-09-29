package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after;

import java.math.BigDecimal;

/**
 * What <em>every</em> account can do. Withdrawing is not in this contract, so no subtype has to refuse it.
 *
 * @see "m01 lesson, section LSP"
 */
public sealed interface Account permits CheckingAccount, FixedDepositAccount {

    String id();

    BigDecimal balance();

    void deposit(BigDecimal amount);

    /** Shared validation: amounts must be positive. */
    static void requirePositive(BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
    }
}
