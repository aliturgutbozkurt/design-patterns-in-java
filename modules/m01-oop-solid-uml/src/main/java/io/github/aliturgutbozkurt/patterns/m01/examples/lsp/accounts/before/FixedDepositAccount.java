package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.before;

import java.math.BigDecimal;

/**
 * LSP violation: the subtype <em>strengthens the precondition</em> of {@code withdraw} (no amount is allowed), so
 * code that is correct for every {@link Account} fails for this one.
 *
 * @see "m01 lesson, section LSP"
 */
public final class FixedDepositAccount extends Account {

    public FixedDepositAccount(String id, BigDecimal openingBalance) {
        super(id, openingBalance);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        throw new UnsupportedOperationException("no withdrawals from a fixed deposit");
    }
}
