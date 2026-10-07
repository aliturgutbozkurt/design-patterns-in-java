package io.github.aliturgutbozkurt.patterns.capstone.api.checkout;

import java.util.NoSuchElementException;

/**
 * GIVEN — do not modify. Inbound port of features F5 (validation) and F6 (payment): turns an open cart into a paid
 * order, or reports every reason why not.
 *
 * @see "capstone brief §2.2 — Checkout (F5, F6)"
 */
public interface CheckoutUseCase {

    /**
     * Validates, quotes, charges exactly once and places the order (see brief §2.2 for the rule order and the reason
     * texts). An unknown cart throws {@link NoSuchElementException}; a closed cart throws {@link IllegalStateException}
     * ({@code cart closed: cart-1}).
     */
    CheckoutResult checkout(CheckoutRequest request);
}
