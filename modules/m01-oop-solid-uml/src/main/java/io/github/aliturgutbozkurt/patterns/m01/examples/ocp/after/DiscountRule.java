package io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after;

import java.math.BigDecimal;

/**
 * OCP: the extension point. A new discount is a new implementation — often just a lambda — never an edit to
 * {@link Checkout}.
 *
 * @see "m01 lesson, section OCP"
 */
@FunctionalInterface
public interface DiscountRule {

    /** Returns the price after this discount. */
    BigDecimal apply(BigDecimal price);
}
