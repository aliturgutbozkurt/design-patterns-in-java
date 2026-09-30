package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.payment;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.payment.acme.AcmePayClient;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.PaymentPort;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import java.util.Objects;

/**
 * Adapter (m04) as an outbound adapter: makes the provider's status-code API look like the core's
 * {@link PaymentPort}. A declined card is a business answer; an unknown code is an infrastructure failure.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class LegacyPaymentAdapter implements PaymentPort {

    private final AcmePayClient client;

    public LegacyPaymentAdapter(AcmePayClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    @Override
    public boolean charge(String customer, Money amount) {
        int code = client.pay(customer, amount.cents());
        return switch (code) {
            case 0 -> true;   // approved
            case 51 -> false; // insufficient funds: declined
            default -> throw new IllegalStateException("AcmePay failed with status code " + code);
        };
    }
}
