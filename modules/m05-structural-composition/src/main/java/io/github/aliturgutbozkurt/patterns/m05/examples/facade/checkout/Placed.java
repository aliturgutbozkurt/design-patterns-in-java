package io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout;

/**
 * The order went through: stock reserved, card charged, parcel on its way.
 *
 * @see "m05 lesson, section Facade — modern Java 27"
 */
public record Placed(String trackingNumber, long totalCents) implements CheckoutResult {}
