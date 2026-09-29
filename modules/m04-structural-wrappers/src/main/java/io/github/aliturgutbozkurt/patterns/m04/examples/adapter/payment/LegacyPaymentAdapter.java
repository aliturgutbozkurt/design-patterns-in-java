package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment;

import java.util.Objects;

/**
 * Object adapter as a record: implements the target and holds the adaptee.
 *
 * @see "m04 lesson, section Adapter"
 */
public record LegacyPaymentAdapter(LegacyPayGateway gateway) implements PaymentProcessor {

    public LegacyPaymentAdapter {
        Objects.requireNonNull(gateway, "gateway");
    }

    @Override
    public PaymentResult pay(PaymentRequest request) {
        String amount = LegacyPaymentMapping.toLegacyAmount(request.amountMinor());   // validates first
        int status = gateway.makePayment(request.cardNumber(), amount, request.currency());
        return LegacyPaymentMapping.toResult(status, gateway.lastTransactionId());
    }
}
