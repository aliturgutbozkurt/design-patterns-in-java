package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.after;

import java.util.Objects;

/**
 * Rich domain model: the rules live next to the data they protect, and there is no setter to go around them. Every
 * failed operation leaves the balance unchanged.
 *
 * @see "m11 lesson, section Anti-patterns — anaemic domain model"
 */
public final class Account {

    private final String owner;
    private Money balance = Money.ZERO;
    private boolean closed;

    public Account(String owner) {
        this.owner = Objects.requireNonNull(owner, "owner");
    }

    public void deposit(Money amount) {
        requireUsable(amount);
        balance = balance.plus(amount);
    }

    public void withdraw(Money amount) {
        requireUsable(amount);
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("insufficient funds");
        }
        balance = balance.minus(amount);
    }

    public void close() {
        closed = true;
    }

    public Money balance() {
        return balance;
    }

    public boolean isClosed() {
        return closed;
    }

    public String owner() {
        return owner;
    }

    private void requireUsable(Money amount) {
        if (Objects.requireNonNull(amount, "amount").cents() == 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (closed) {
            throw new IllegalStateException("account is closed");
        }
    }
}
