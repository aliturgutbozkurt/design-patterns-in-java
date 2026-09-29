package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.accounts.after;

import java.math.BigDecimal;

/**
 * A capability: only types that can honour "any positive amount up to the balance" implement it.
 *
 * @see "m01 lesson, section LSP"
 */
public interface Withdrawable {

    void withdraw(BigDecimal amount);

    BigDecimal balance();
}
