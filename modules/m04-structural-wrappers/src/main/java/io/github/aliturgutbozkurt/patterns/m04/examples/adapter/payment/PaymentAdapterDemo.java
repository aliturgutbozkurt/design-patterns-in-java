package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment;

import io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment.PaymentResult.Approved;
import io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment.PaymentResult.Declined;
import java.math.BigDecimal;
import java.util.List;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/adapter/payment/PaymentAdapterDemo.java} */
public final class PaymentAdapterDemo {

    private PaymentAdapterDemo() {}

    private static final List<PaymentRequest> ORDERS = List.of(
            new PaymentRequest("4111111111111111", 1250, "TRY"),
            new PaymentRequest("4000000000000051", 700, "TRY"),
            new PaymentRequest("4000000000000054", 9990, "EUR"),
            new PaymentRequest("4000000000000096", 100, "USD"));

    public static void main(String[] args) {
        PaymentProcessor objectAdapter = new LegacyPaymentAdapter(new LegacyPayGateway());
        PaymentProcessor classAdapter = new LegacyPaymentClassAdapter();

        System.out.println("object adapter (record LegacyPaymentAdapter)");
        checkout(objectAdapter);
        System.out.println("class adapter (LegacyPaymentClassAdapter extends LegacyPayGateway)");
        checkout(classAdapter);
        try {
            objectAdapter.pay(new PaymentRequest("4111111111111111", 0, "TRY"));
        } catch (IllegalArgumentException e) {
            System.out.println("rejected before the gateway: " + e.getMessage());
        }
    }

    /** Checkout code: knows only the target interface and the sealed result. */
    private static void checkout(PaymentProcessor processor) {
        for (PaymentRequest order : ORDERS) {
            String outcome = switch (processor.pay(order)) {                 // exhaustive: no default needed
                case Approved(String transactionId) -> "approved, transaction " + transactionId;
                case Declined(DeclineReason reason, String message) -> "declined (" + reason + "): " + message;
            };
            System.out.println("  " + BigDecimal.valueOf(order.amountMinor(), 2) + " " + order.currency()
                    + " on card ending " + order.cardNumber().substring(12) + ": " + outcome);
        }
    }
}
