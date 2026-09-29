package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * A bank account. Contract of {@code withdraw}: any positive amount up to the balance succeeds.
 *
 * @see "m01 lesson, section LSP"
 */
public class Account {

    private final String id;
    private BigDecimal balance;

    public Account(String id, BigDecimal openingBalance) {
        this.id = Objects.requireNonNull(id, "id");
        if (openingBalance.signum() < 0) {
            throw new IllegalArgumentException("opening balance must not be negative: " + openingBalance);
        }
        this.balance = openingBalance.setScale(2, RoundingMode.HALF_EVEN);
    }

    public String id() {
        return id;
    }

    public BigDecimal balance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        requirePositive(amount);
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        requirePositive(amount);
        if (amount.compareTo(balance) > 0) {
            throw new IllegalStateException("insufficient funds: balance " + balance + ", requested " + amount);
        }
        balance = balance.subtract(amount);
    }

    static void requirePositive(BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
    }
}
