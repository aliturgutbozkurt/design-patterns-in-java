package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * A fixed deposit: money goes in, nothing comes out before maturity — and the type says so.
 *
 * @see "m01 lesson, section LSP"
 */
public final class FixedDepositAccount implements Account {

    private final String id;
    private BigDecimal balance;

    public FixedDepositAccount(String id, BigDecimal openingBalance) {
        this.id = Objects.requireNonNull(id, "id");
        if (openingBalance.signum() < 0) {
            throw new IllegalArgumentException("opening balance must not be negative: " + openingBalance);
        }
        this.balance = openingBalance.setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public BigDecimal balance() {
        return balance;
    }

    @Override
    public void deposit(BigDecimal amount) {
        Account.requirePositive(amount);
        balance = balance.add(amount);
    }
}
