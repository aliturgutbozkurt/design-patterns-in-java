package io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Subsystem (in-memory fake): charges cards, declines the numbers it was told to, and refunds charges.
 *
 * @see "m05 lesson, section Facade — modern Java 27"
 */
public final class PaymentGateway {

    private final Set<String> declinedCards;
    private final Map<String, Long> charges = new HashMap<>();
    private int chargeCount;
    private int refundCount;

    public PaymentGateway(Set<String> declinedCardNumbers) {
        this.declinedCards = Set.copyOf(declinedCardNumbers);
    }

    /** Returns a payment id, or empty when the card is declined. */
    public Optional<String> charge(Card card, long cents) {
        if (declinedCards.contains(card.number())) {
            return Optional.empty();
        }
        chargeCount++;
        String id = "P-" + chargeCount;
        charges.put(id, cents);
        return Optional.of(id);
    }

    /** Gives the money of one charge back; unknown or already refunded ids are ignored. */
    public void refund(String paymentId) {
        if (charges.remove(paymentId) != null) {
            refundCount++;
        }
    }

    /** Money kept: everything charged minus everything refunded. */
    public long netChargedCents() {
        return charges.values().stream().mapToLong(Long::longValue).sum();
    }

    public int chargeCount() {
        return chargeCount;
    }

    public int refundCount() {
        return refundCount;
    }
}
