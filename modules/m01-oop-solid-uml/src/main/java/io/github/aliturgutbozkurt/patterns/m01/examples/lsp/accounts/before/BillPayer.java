package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before;

import java.math.BigDecimal;

/**
 * Client code written against {@link Account}'s contract.
 *
 * @see "m01 lesson, section LSP"
 */
public final class BillPayer {

    private BillPayer() {}

    /** Pays a bill from the account and returns the remaining balance. */
    public static BigDecimal pay(Account account, BigDecimal bill) {
        account.withdraw(bill);
        return account.balance();
    }
}
