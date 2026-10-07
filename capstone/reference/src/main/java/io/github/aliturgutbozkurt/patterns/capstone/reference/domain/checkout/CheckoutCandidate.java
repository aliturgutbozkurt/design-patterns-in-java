package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.checkout;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import java.util.List;
import java.util.Objects;

/**
 * Everything checkout validation looks at, collected before anything is charged.
 *
 * @param lines         the cart lines in cart order
 * @param address       the shipping address
 * @param coupon        the cart's coupon code, {@code ""} when none
 * @param couponExpired whether that coupon is past its last valid day
 * @param cardToken     the card token, blank when missing
 * @see "capstone guide, Pattern map — Chain of Responsibility"
 */
public record CheckoutCandidate(List<CandidateLine> lines, Address address, String coupon, boolean couponExpired,
                                String cardToken) {

    public CheckoutCandidate {
        lines = List.copyOf(lines);
        Objects.requireNonNull(address, "address");
        Objects.requireNonNull(coupon, "coupon");
        Objects.requireNonNull(cardToken, "cardToken");
    }
}
