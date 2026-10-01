package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The class under test: five collaborators, each a different kind of test double in the tests.
 *
 * @see "m11 lesson, section Test doubles"
 */
public final class CheckoutService {

    private final PriceLookup prices;
    private final PaymentGateway payments;
    private final ReceiptRepository receipts;
    private final Notifier notifier;
    private final AuditLog audit;

    public CheckoutService(PriceLookup prices, PaymentGateway payments, ReceiptRepository receipts,
                           Notifier notifier, AuditLog audit) {
        this.prices = Objects.requireNonNull(prices, "prices");
        this.payments = Objects.requireNonNull(payments, "payments");
        this.receipts = Objects.requireNonNull(receipts, "receipts");
        this.notifier = Objects.requireNonNull(notifier, "notifier");
        this.audit = Objects.requireNonNull(audit, "audit");
    }

    /** Charges the sum of the prices; a receipt if approved, empty (and an audit entry) if declined. */
    public Optional<Receipt> checkout(String customer, List<String> skus) {
        long total = skus.stream().mapToLong(prices::priceCents).sum();
        Optional<String> transaction = payments.charge(customer, total);
        if (transaction.isEmpty()) {
            audit.record("declined " + customer + " " + total);
            return Optional.empty();
        }
        var receipt = new Receipt(transaction.get(), customer, skus, total);
        receipts.save(receipt);
        notifier.notify(customer, "receipt " + receipt.id() + " for " + BigDecimal.valueOf(total, 2));
        return Optional.of(receipt);
    }
}
