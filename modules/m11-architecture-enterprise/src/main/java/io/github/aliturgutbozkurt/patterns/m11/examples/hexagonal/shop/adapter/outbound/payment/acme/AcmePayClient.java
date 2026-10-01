package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.payment.acme;

/**
 * Stand-in for a payment provider's SDK that we cannot change: status codes instead of types — {@code 0} approved,
 * {@code 51} insufficient funds, anything else a provider error.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
@FunctionalInterface
public interface AcmePayClient {

    /** Charges {@code cents} to {@code account} and returns a status code. */
    int pay(String account, long cents);
}
