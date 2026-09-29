package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment;

import io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment.PaymentResult.Approved;
import io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment.PaymentResult.Declined;
import java.math.BigDecimal;

/** The translation both payment adapters share: our types to legacy values and back. */
final class LegacyPaymentMapping {

    private LegacyPaymentMapping() {}

    static String toLegacyAmount(long amountMinor) {
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amountMinor);
        }
        return BigDecimal.valueOf(amountMinor, 2).toPlainString();   // 1250 -> "12.50"
    }

    static PaymentResult toResult(int status, String transactionId) {
        return switch (status) {
            case LegacyPayGateway.OK -> new Approved(transactionId);
            case LegacyPayGateway.INSUFFICIENT_FUNDS ->
                    new Declined(DeclineReason.INSUFFICIENT_FUNDS, "insufficient funds");
            case LegacyPayGateway.CARD_EXPIRED -> new Declined(DeclineReason.CARD_EXPIRED, "card expired");
            default -> new Declined(DeclineReason.UNKNOWN, "legacy status " + status);
        };
    }
}
