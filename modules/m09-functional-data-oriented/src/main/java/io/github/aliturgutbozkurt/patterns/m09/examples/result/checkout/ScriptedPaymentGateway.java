package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import java.util.Optional;
import java.util.Set;

/**
 * A fake payment gateway: declines the cards it was given, approves every other card with ids {@code PAY-1},
 * {@code PAY-2}, … and counts its calls.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public final class ScriptedPaymentGateway {

    private final Set<String> declinedCards;
    private int calls;
    private int approved;

    public ScriptedPaymentGateway(Set<String> declinedCards) {
        this.declinedCards = Set.copyOf(declinedCards);
    }

    /** The payment id, or empty if the card was declined. */
    public Optional<String> charge(String card, long amountCents) {
        calls++;
        if (declinedCards.contains(card)) {
            return Optional.empty();
        }
        approved++;
        return Optional.of("PAY-" + approved);
    }

    public int calls() {
        return calls;
    }
}
