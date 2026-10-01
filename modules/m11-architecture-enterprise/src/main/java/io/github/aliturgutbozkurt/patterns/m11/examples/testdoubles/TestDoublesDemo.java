package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles;

import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.CheckoutService;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles.DummyAuditLog;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles.FakeReceiptRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles.MockPaymentGateway;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles.SpyNotifier;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles.StubPriceLookup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/testdoubles/TestDoublesDemo.java}
 *
 * @see "m11 lesson, section Test doubles"
 */
public final class TestDoublesDemo {

    private TestDoublesDemo() {}

    public static void main(String[] args) {
        var prices = new StubPriceLookup(Map.of("BOOK-1", 2000L, "PEN-7", 700L));
        var payments = new MockPaymentGateway().expectCharge("alice", 2700);
        var receipts = new FakeReceiptRepository();
        var notifier = new SpyNotifier();
        var service = new CheckoutService(prices, payments, receipts, notifier, new DummyAuditLog());

        var receipt = service.checkout("alice", List.of("BOOK-1", "PEN-7")).orElseThrow();
        payments.verify();
        System.out.println("mock verified: alice charged 2700");
        System.out.println("fake finds the receipt: " + receipts.findById(receipt.id()).isPresent());
        System.out.println("spy recorded: " + notifier.calls());

        try {
            service.checkout("alice", List.of("BOOK-1")); // no charge expected any more
        } catch (AssertionError e) {
            System.out.println("mock, unexpected call: " + e.getMessage());
        }
        try {
            new MockPaymentGateway().expectCharge("bob", 700).verify();
        } catch (AssertionError e) {
            System.out.println("mock, verify: " + e.getMessage());
        }

        List<String> audit = new ArrayList<>();
        var declining = new CheckoutService(prices, new MockPaymentGateway().expectDeclinedCharge("carol", 700),
                receipts, notifier, audit::add);
        System.out.println("declined: " + declining.checkout("carol", List.of("PEN-7")) + ", audit " + audit);
    }
}
