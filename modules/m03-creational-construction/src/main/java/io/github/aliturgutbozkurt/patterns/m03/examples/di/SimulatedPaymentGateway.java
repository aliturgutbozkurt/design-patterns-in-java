package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import java.math.BigDecimal;

/**
 * Stands in for a real payment provider: numbers receipts {@code PAY-1}, {@code PAY-2}, ….
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
public final class SimulatedPaymentGateway implements PaymentGateway {

    private int nextReceipt = 1;

    @Override
    public synchronized String charge(String customer, BigDecimal amount) {
        return "PAY-" + nextReceipt++;
    }
}
