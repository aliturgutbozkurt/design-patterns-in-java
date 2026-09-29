package io.github.aliturgutbozkurt.patterns.m01.examples.srp.after;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Keeps invoices in a map — enough for the demo and the tests.
 *
 * @see "m01 lesson, section SRP"
 */
public final class InMemoryInvoiceRepository implements InvoiceRepository {

    private final Map<String, Invoice> invoices = new LinkedHashMap<>();

    @Override
    public void save(Invoice invoice) {
        invoices.put(invoice.number(), invoice);
    }

    @Override
    public Optional<Invoice> findByNumber(String number) {
        return Optional.ofNullable(invoices.get(number));
    }
}
