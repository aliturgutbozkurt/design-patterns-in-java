package io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout;

/**
 * The order did not go through; every earlier step has already been undone.
 *
 * @see "m05 lesson, section Facade — modern Java 27"
 */
public record Rejected(Reason reason, String detail) implements CheckoutResult {

    /** Why an order was rejected. */
    public enum Reason { OUT_OF_STOCK, PAYMENT_DECLINED, SHIPPING_UNAVAILABLE }
}
