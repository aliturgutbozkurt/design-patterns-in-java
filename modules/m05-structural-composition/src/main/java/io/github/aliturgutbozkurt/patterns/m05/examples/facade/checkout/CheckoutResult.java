package io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout;

/**
 * What {@link CheckoutFacade#placeOrder} returns: expected business failures are values, not exceptions, so the
 * caller must handle both outcomes in an exhaustive {@code switch}.
 *
 * @see "m05 lesson, section Facade — modern Java 27"
 */
public sealed interface CheckoutResult permits Placed, Rejected {}
