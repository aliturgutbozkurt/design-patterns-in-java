package io.github.aliturgutbozkurt.patterns.capstone.api.checkout;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import java.util.Objects;

/**
 * GIVEN — do not modify. A checkout attempt.
 *
 * @param cart            the cart to buy
 * @param shippingAddress where to ship (may be incomplete — that is a validation result, not an exception)
 * @param cardToken       the payment provider's card token; blank means missing
 * @see "capstone brief, Business rules — Checkout (F5, F6)"
 */
public record CheckoutRequest(CartId cart, Address shippingAddress, String cardToken) {

    public CheckoutRequest {
        Objects.requireNonNull(cart, "cart");
        Objects.requireNonNull(shippingAddress, "shippingAddress");
        Objects.requireNonNull(cardToken, "cardToken");
    }
}
