package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.before;

/**
 * ANTI-PATTERN — the other half of the anaemic model: all the rules, applied to someone else's data. They hold only
 * for callers who remember to go through this service.
 *
 * @see "m11 lesson, section Anti-patterns — anaemic domain model"
 */
public final class AccountService {

    public void deposit(Account account, long cents) {
        if (cents <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (account.isClosed()) {
            throw new IllegalStateException("account is closed");
        }
        account.setBalanceCents(account.getBalanceCents() + cents);
    }

    public void withdraw(Account account, long cents) {
        if (cents <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (account.isClosed()) {
            throw new IllegalStateException("account is closed");
        }
        if (cents > account.getBalanceCents()) {
            throw new IllegalArgumentException("insufficient funds");
        }
        account.setBalanceCents(account.getBalanceCents() - cents);
    }

    public void close(Account account) {
        account.setClosed(true);
    }
}
