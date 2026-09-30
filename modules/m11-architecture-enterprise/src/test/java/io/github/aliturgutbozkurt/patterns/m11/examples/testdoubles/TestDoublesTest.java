package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.CheckoutService;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.Receipt;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles.DummyAuditLog;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles.FakeReceiptRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles.MockPaymentGateway;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles.SpyNotifier;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles.StubPriceLookup;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TestDoublesTest {

    private final StubPriceLookup prices = new StubPriceLookup(Map.of("BOOK-1", 2000L, "PEN-7", 700L));
    private final MockPaymentGateway payments = new MockPaymentGateway();
    private final FakeReceiptRepository receipts = new FakeReceiptRepository();
    private final SpyNotifier notifier = new SpyNotifier();
    private final CheckoutService service = new CheckoutService(prices, payments, receipts, notifier,
            new DummyAuditLog());

    @Test
    void checkoutWithStubPricesChargesTheExpectedAmount() {
        payments.expectCharge("alice", 2700);
        service.checkout("alice", List.of("BOOK-1", "PEN-7"));
        payments.verify(); // interaction verification
    }

    @Test
    void unexpectedChargeFailsImmediatelyNamingExpectedAndActual() {
        payments.expectCharge("alice", 2000);
        assertThatThrownBy(() -> service.checkout("alice", List.of("BOOK-1", "PEN-7")))
                .isInstanceOf(AssertionError.class)
                .hasMessage("unexpected charge alice 2700, expected alice 2000");
        assertThat(notifier.calls()).isEmpty();
    }

    @Test
    void verifyReportsAMissingExpectedCall() {
        payments.expectCharge("alice", 2700).expectCharge("bob", 700);
        service.checkout("alice", List.of("BOOK-1", "PEN-7"));
        assertThatThrownBy(payments::verify).isInstanceOf(AssertionError.class)
                .hasMessage("missing expected charge: bob 700");
    }

    @Test
    void spyRecordsNotificationsWithArgumentsInOrder() {
        payments.expectCharge("alice", 2000).expectCharge("bob", 700);
        service.checkout("alice", List.of("BOOK-1"));
        service.checkout("bob", List.of("PEN-7"));
        assertThat(notifier.calls()).containsExactly("alice: receipt TX-1 for 20.00", "bob: receipt TX-2 for 7.00");
    }

    @Test
    void fakeRepositoryFindsTheSavedReceipt() {
        payments.expectCharge("alice", 2700);
        Receipt receipt = service.checkout("alice", List.of("BOOK-1", "PEN-7")).orElseThrow();
        assertThat(receipts.findById("TX-1")).contains(receipt); // state verification
        assertThat(receipt).isEqualTo(new Receipt("TX-1", "alice", List.of("BOOK-1", "PEN-7"), 2700));
    }

    @Test
    void dummyThrowsIfTheServiceTouchesTheAuditLog() {
        payments.expectDeclinedCharge("alice", 700);
        assertThatThrownBy(() -> service.checkout("alice", List.of("PEN-7"))).isInstanceOf(AssertionError.class)
                .hasMessage("the audit log must not be used here, but got: declined alice 700");
    }

    @Test
    void declinedChargeIsAuditedAndProducesNoReceipt() {
        List<String> audit = new ArrayList<>();
        var declining = new CheckoutService(prices, payments.expectDeclinedCharge("carol", 700), receipts, notifier,
                audit::add);
        assertThat(declining.checkout("carol", List.of("PEN-7"))).isEmpty();
        assertThat(audit).containsExactly("declined carol 700");
        assertThat(notifier.calls()).isEmpty();
    }

    @Test
    void demoPrintsEveryKindOfDouble() {
        assertThat(Console.capture(() -> TestDoublesDemo.main(new String[0]))).isEqualTo("""
                mock verified: alice charged 2700
                fake finds the receipt: true
                spy recorded: [alice: receipt TX-1 for 27.00]
                mock, unexpected call: unexpected charge alice 2000, expected no charge
                mock, verify: missing expected charge: bob 700
                declined: Optional.empty, audit [declined carol 700]
                """);
    }
}
