package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment;

/**
 * Class adapter: <em>is a</em> legacy gateway that also implements the target, so the legacy methods leak to clients.
 *
 * @see "m04 lesson, section Adapter"
 */
public class LegacyPaymentClassAdapter extends LegacyPayGateway implements PaymentProcessor {

    @Override
    public PaymentResult pay(PaymentRequest request) {
        String amount = LegacyPaymentMapping.toLegacyAmount(request.amountMinor());
        int status = makePayment(request.cardNumber(), amount, request.currency());   // inherited, not delegated
        return LegacyPaymentMapping.toResult(status, lastTransactionId());
    }
}
