package io.github.aliturgutbozkurt.patterns.m11.examples.erosion.adapter;

import io.github.aliturgutbozkurt.patterns.m11.examples.erosion.domain.Invoice;

/**
 * The adapter half of the cycle: it takes an {@link Invoice} (adapter → domain, which is fine) while the invoice
 * calls it (domain → adapter, which is not).
 *
 * @see "m11 lesson, section Architecture rules"
 */
public final class InvoiceDao {

    public String store(Invoice invoice) {
        return "stored invoice " + invoice.number() + " (" + invoice.amount() + ")";
    }
}
