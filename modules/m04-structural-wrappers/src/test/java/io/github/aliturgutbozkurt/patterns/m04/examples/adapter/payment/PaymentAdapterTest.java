package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment.PaymentResult.Approved;
import io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment.PaymentResult.Declined;
import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class PaymentAdapterTest {

    private static final String GOOD_CARD = "4111111111111111";

    /** A legacy gateway that remembers the amounts it was asked to charge. */
    private static final class RecordingGateway extends LegacyPayGateway {
        final List<String> amounts = new ArrayList<>();

        @Override
        public int makePayment(String cardNumber, String amount, String currencyCode) {
            amounts.add(amount);
            return super.makePayment(cardNumber, amount, currencyCode);
        }
    }

    @Test
    void convertsMinorUnitsToTheLegacyDecimalString() {
        var gateway = new RecordingGateway();
        new LegacyPaymentAdapter(gateway).pay(new PaymentRequest(GOOD_CARD, 1250, "TRY"));
        new LegacyPaymentAdapter(gateway).pay(new PaymentRequest(GOOD_CARD, 5, "TRY"));
        assertThat(gateway.amounts).containsExactly("12.50", "0.05");
    }

    @Test
    void statusZeroIsApprovedWithTheTransactionId() {
        PaymentProcessor processor = new LegacyPaymentAdapter(new LegacyPayGateway());
        assertThat(processor.pay(new PaymentRequest(GOOD_CARD, 1250, "TRY"))).isEqualTo(new Approved("TX-1001"));
        assertThat(processor.pay(new PaymentRequest(GOOD_CARD, 990, "EUR"))).isEqualTo(new Approved("TX-1002"));
    }

    @Test
    void knownLegacyCodesBecomeDeclineReasons() {
        PaymentProcessor processor = new LegacyPaymentAdapter(new LegacyPayGateway());
        assertThat(processor.pay(new PaymentRequest("4000000000000051", 1250, "TRY")))
                .isEqualTo(new Declined(DeclineReason.INSUFFICIENT_FUNDS, "insufficient funds"));
        assertThat(processor.pay(new PaymentRequest("4000000000000054", 1250, "TRY")))
                .isEqualTo(new Declined(DeclineReason.CARD_EXPIRED, "card expired"));
    }

    @Test
    void unknownLegacyCodeIsDeclinedWithTheCodeInTheMessage() {
        PaymentResult result = new LegacyPaymentAdapter(new LegacyPayGateway())
                .pay(new PaymentRequest("4000000000000096", 1250, "TRY"));
        assertThat(result).isEqualTo(new Declined(DeclineReason.UNKNOWN, "legacy status 96"));
    }

    @Test
    void objectAndClassAdaptersReturnEqualResults() {
        List<PaymentRequest> requests = List.of(
                new PaymentRequest(GOOD_CARD, 1250, "TRY"),
                new PaymentRequest("4000000000000051", 700, "TRY"),
                new PaymentRequest("4000000000000054", 700, "USD"),
                new PaymentRequest("4000000000000096", 700, "USD"),
                new PaymentRequest(GOOD_CARD, 1, "EUR"));
        PaymentProcessor objectAdapter = new LegacyPaymentAdapter(new LegacyPayGateway());
        PaymentProcessor classAdapter = new LegacyPaymentClassAdapter();
        Function<PaymentRequest, PaymentResult> viaObject = objectAdapter::pay;
        Function<PaymentRequest, PaymentResult> viaClass = classAdapter::pay;
        assertThat(requests.stream().map(viaClass).toList()).isEqualTo(requests.stream().map(viaObject).toList());
    }

    @Test
    void classAdapterLeaksTheLegacyApiObjectAdapterDoesNot() {
        PaymentProcessor classAdapter = new LegacyPaymentClassAdapter();
        PaymentProcessor objectAdapter = new LegacyPaymentAdapter(new LegacyPayGateway());
        assertThat(classAdapter).isInstanceOf(LegacyPayGateway.class);
        assertThat(objectAdapter).isNotInstanceOf(LegacyPayGateway.class);
        // a client can bypass the new interface (and its validation) on the class adapter:
        var leaked = (LegacyPayGateway) classAdapter;
        assertThat(leaked.makePayment(GOOD_CARD, "-5.00", "TRY")).isEqualTo(LegacyPayGateway.OK);
    }

    @Test
    void rejectsNonPositiveAmountsBeforeCallingTheGateway() {
        var gateway = new RecordingGateway();
        PaymentProcessor processor = new LegacyPaymentAdapter(gateway);
        assertThatIllegalArgumentException().isThrownBy(() -> processor.pay(new PaymentRequest(GOOD_CARD, 0, "TRY")))
                .withMessage("amount must be positive: 0");
        assertThatIllegalArgumentException().isThrownBy(() -> processor.pay(new PaymentRequest(GOOD_CARD, -1, "TRY")));
        assertThat(gateway.amounts).isEmpty();

        var classAdapter = new LegacyPaymentClassAdapter() {
            int calls;

            @Override
            public int makePayment(String cardNumber, String amount, String currencyCode) {
                calls++;
                return super.makePayment(cardNumber, amount, currencyCode);
            }
        };
        assertThatIllegalArgumentException().isThrownBy(() -> classAdapter.pay(new PaymentRequest(GOOD_CARD, 0, "TRY")));
        assertThat(classAdapter.calls).isZero();
    }

    @Test
    void rejectsMissingParts() {
        assertThatNullPointerException().isThrownBy(() -> new LegacyPaymentAdapter(null));
        assertThatNullPointerException().isThrownBy(() -> new PaymentRequest(null, 100, "TRY"));
        assertThatNullPointerException().isThrownBy(() -> new PaymentRequest(GOOD_CARD, 100, null));
    }

    @Test
    void demoPrintsBothAdaptersSideBySide() {
        assertThat(Console.capture(() -> PaymentAdapterDemo.main(new String[0]))).isEqualTo("""
                object adapter (record LegacyPaymentAdapter)
                  12.50 TRY on card ending 1111: approved, transaction TX-1001
                  7.00 TRY on card ending 0051: declined (INSUFFICIENT_FUNDS): insufficient funds
                  99.90 EUR on card ending 0054: declined (CARD_EXPIRED): card expired
                  1.00 USD on card ending 0096: declined (UNKNOWN): legacy status 96
                class adapter (LegacyPaymentClassAdapter extends LegacyPayGateway)
                  12.50 TRY on card ending 1111: approved, transaction TX-1001
                  7.00 TRY on card ending 0051: declined (INSUFFICIENT_FUNDS): insufficient funds
                  99.90 EUR on card ending 0054: declined (CARD_EXPIRED): card expired
                  1.00 USD on card ending 0096: declined (UNKNOWN): legacy status 96
                rejected before the gateway: amount must be positive: 0
                """);
    }
}
