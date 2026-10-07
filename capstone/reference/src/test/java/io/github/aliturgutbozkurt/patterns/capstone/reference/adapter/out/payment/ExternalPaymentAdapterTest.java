package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.payment;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.ExternalPaymentApi;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.GatewayResponse;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentOutcome;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExternalPaymentAdapterTest {

    /** Hand-written stub of the provider: answers with a fixed status and records the calls. */
    private static final class StubProvider implements ExternalPaymentApi {
        private final int status;
        private final List<List<String>> calls = new ArrayList<>();

        StubProvider(int status) {
            this.status = status;
        }

        @Override
        public GatewayResponse authorize(String merchantId, String cardToken, String amount, String currency,
                                         String idempotencyKey) {
            calls.add(List.of(merchantId, cardToken, amount, currency, idempotencyKey));
            return new GatewayResponse(status, status == 200 ? "txn-9" : "", "msg");
        }

        @Override
        public GatewayResponse refund(String merchantId, String reference, String amount, String currency) {
            calls.add(List.of(merchantId, reference, amount, currency));
            return new GatewayResponse(status, status == 200 ? "rfd-1" : "", "msg");
        }
    }

    @Test
    void translatesMoneyAndIdsIntoTheProvidersFormat() {
        StubProvider provider = new StubProvider(200);
        var adapter = new ExternalPaymentAdapter(provider, "SHOP");

        adapter.charge(Money.of("987.91"), "tok", "cart-1");
        adapter.refund("txn-9", Money.of("5"));

        assertThat(provider.calls).containsExactly(List.of("SHOP", "tok", "987.91", "TRY", "cart-1"),
                List.of("SHOP", "txn-9", "5.00", "TRY"));
    }

    @Test
    void translatesStatusCodesIntoOutcomes() {
        assertThat(new ExternalPaymentAdapter(new StubProvider(200), "SHOP").charge(Money.ZERO, "t", "c"))
                .isEqualTo(new PaymentOutcome.Approved("txn-9"));
        assertThat(new ExternalPaymentAdapter(new StubProvider(402), "SHOP").charge(Money.ZERO, "t", "c"))
                .isInstanceOf(PaymentOutcome.Declined.class);
        assertThat(new ExternalPaymentAdapter(new StubProvider(503), "SHOP").charge(Money.ZERO, "t", "c"))
                .isInstanceOf(PaymentOutcome.Unavailable.class);
        assertThat(new ExternalPaymentAdapter(new StubProvider(500), "SHOP").refund("txn-9", Money.ZERO))
                .isInstanceOf(PaymentOutcome.Unavailable.class);
    }
}
