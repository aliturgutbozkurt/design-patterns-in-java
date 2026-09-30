package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.payment.acme;

import java.util.ArrayList;
import java.util.List;

/**
 * The provider's sandbox: approves every charge up to a credit limit, declines ({@code 51}) above it, and remembers
 * each call so demos and tests can see what was charged.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class AcmePaySandbox implements AcmePayClient {

    public static final int APPROVED = 0;
    public static final int INSUFFICIENT_FUNDS = 51;

    private final long creditLimitCents;
    private final List<String> charges = new ArrayList<>();

    public AcmePaySandbox(long creditLimitCents) {
        this.creditLimitCents = creditLimitCents;
    }

    @Override
    public int pay(String account, long cents) {
        charges.add(account + " " + cents);
        return cents <= creditLimitCents ? APPROVED : INSUFFICIENT_FUNDS;
    }

    /** Every call as {@code "<account> <cents>"}, oldest first (an unmodifiable copy). */
    public List<String> charges() {
        return List.copyOf(charges);
    }
}
