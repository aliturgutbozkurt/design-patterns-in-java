package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain;

import java.util.Objects;

/**
 * A rich domain entity: the balance can only change through {@link #withdraw} and {@link #deposit}, and it can never
 * become negative. Knows nothing about storage, text or the use case that calls it.
 *
 * @see "m11 lesson, section Ports and Adapters"
 */
public final class Account {

    private final AccountId id;
    private Money balance;

    public Account(AccountId id, Money balance) {
        this.id = Objects.requireNonNull(id, "id");
        this.balance = Objects.requireNonNull(balance, "balance");
    }

    public AccountId id() {
        return id;
    }

    public Money balance() {
        return balance;
    }

    /** Whether {@code amount} can be withdrawn without going below zero. */
    public boolean canWithdraw(Money amount) {
        return balance.compareTo(amount) >= 0;
    }

    public void withdraw(Money amount) {
        if (!canWithdraw(amount)) {
            throw new IllegalStateException("insufficient funds");
        }
        balance = balance.minus(amount);
    }

    public void deposit(Money amount) {
        balance = balance.plus(amount);
    }
}
