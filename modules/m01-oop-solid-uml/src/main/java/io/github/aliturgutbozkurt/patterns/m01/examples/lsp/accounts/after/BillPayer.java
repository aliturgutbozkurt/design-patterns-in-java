package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after;

import java.math.BigDecimal;

/**
 * Asks for exactly what it needs — something {@link Withdrawable} — so every argument it accepts works.
 *
 * @see "m01 lesson, section LSP"
 */
public final class BillPayer {

    private BillPayer() {}

    /** Pays a bill and returns the remaining balance. */
    public static BigDecimal pay(Withdrawable source, BigDecimal bill) {
        source.withdraw(bill);
        return source.balance();
    }
}
