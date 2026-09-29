package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment;

import java.util.Objects;

/**
 * A payment as our code models it: the amount in minor units (kuruş, cents) and an ISO currency code.
 *
 * @see "m04 lesson, section Adapter"
 */
public record PaymentRequest(String cardNumber, long amountMinor, String currency) {

    public PaymentRequest {
        Objects.requireNonNull(cardNumber, "cardNumber");
        Objects.requireNonNull(currency, "currency");
    }
}
