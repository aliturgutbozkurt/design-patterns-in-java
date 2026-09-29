package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * An everyday account: can deposit and withdraw.
 *
 * @see "m01 lesson, section LSP"
 */
public final class CheckingAccount implements Account, Withdrawable {

    private final String id;
    private BigDecimal balance;

    public CheckingAccount(String id, BigDecimal openingBalance) {
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

    @Override
    public void withdraw(BigDecimal amount) {
        Account.requirePositive(amount);
        if (amount.compareTo(balance) > 0) {
            throw new IllegalStateException("insufficient funds: balance " + balance + ", requested " + amount);
        }
        balance = balance.subtract(amount);
    }
}
