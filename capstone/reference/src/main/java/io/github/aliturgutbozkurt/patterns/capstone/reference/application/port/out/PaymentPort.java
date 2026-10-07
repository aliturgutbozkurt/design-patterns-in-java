package io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;

/**
 * Outbound port: charging and refunding in the shop's own terms (money values, sealed outcomes) — the target
 * interface the payment adapter implements.
 *
 * @see "capstone guide, Pattern map — Adapter"
 */
@PatternRole(value = DesignPattern.ADAPTER, role = "target")
public interface PaymentPort {

    /** Charges a card once; {@code attempt} identifies the attempt (the cart id) for the provider's idempotency. */
    PaymentOutcome charge(Money amount, String cardToken, String attempt);

    /** Refunds an approved payment in full. */
    PaymentOutcome refund(String paymentReference, Money amount);
}
