package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.payment;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.ExternalPaymentApi;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.GatewayResponse;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentOutcome;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentPort;
import java.util.Objects;

/**
 * Makes the third-party payment API fit the {@link PaymentPort}: money becomes {@code "987.91"} in {@code "TRY"} with
 * the merchant id, and status codes become sealed outcomes (200 approved, 402 declined, anything else unavailable).
 * Adapted from modules/m04-…/adapter/payment/LegacyPaymentAdapter.java.
 *
 * @see "capstone guide §1 Pattern map — Adapter"
 */
@PatternRole(value = DesignPattern.ADAPTER, role = "adapter (adaptee: the GIVEN ExternalPaymentApi)")
public final class ExternalPaymentAdapter implements PaymentPort {

    /** The shop's only currency. */
    public static final String CURRENCY = "TRY";

    private final ExternalPaymentApi api;
    private final String merchantId;

    public ExternalPaymentAdapter(ExternalPaymentApi api, String merchantId) {
        this.api = Objects.requireNonNull(api, "api");
        this.merchantId = Objects.requireNonNull(merchantId, "merchantId");
    }

    @Override
    public PaymentOutcome charge(Money amount, String cardToken, String attempt) {
        return outcome(api.authorize(merchantId, cardToken, amount.toPlainString(), CURRENCY, attempt));
    }

    @Override
    public PaymentOutcome refund(String paymentReference, Money amount) {
        return outcome(api.refund(merchantId, paymentReference, amount.toPlainString(), CURRENCY));
    }

    private static PaymentOutcome outcome(GatewayResponse response) {
        return switch (response.status()) {
            case 200 -> new PaymentOutcome.Approved(response.reference());
            case 402 -> new PaymentOutcome.Declined(response.message());
            default -> new PaymentOutcome.Unavailable(response.status() + " " + response.message());
        };
    }
}
