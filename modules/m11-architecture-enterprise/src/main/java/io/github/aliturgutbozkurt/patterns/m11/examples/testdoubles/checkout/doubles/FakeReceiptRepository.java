package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.doubles;

import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.Receipt;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout.ReceiptRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Fake: a real, working implementation with a shortcut (memory instead of a database). Tests check its state.
 *
 * @see "m11 lesson, section Test doubles"
 */
public final class FakeReceiptRepository implements ReceiptRepository {

    private final Map<String, Receipt> receipts = new LinkedHashMap<>();

    @Override
    public void save(Receipt receipt) {
        receipts.put(Objects.requireNonNull(receipt, "receipt").id(), receipt);
    }

    @Override
    public Optional<Receipt> findById(String id) {
        return Optional.ofNullable(receipts.get(id));
    }
}
