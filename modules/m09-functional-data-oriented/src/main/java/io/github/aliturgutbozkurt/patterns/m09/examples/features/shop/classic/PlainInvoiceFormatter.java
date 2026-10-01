package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

import java.util.Locale;

/**
 * The plain-text look of an invoice.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class PlainInvoiceFormatter extends InvoiceFormatter {

    @Override
    protected String header(Order order) {
        return "INVOICE " + order.getId() + " for " + order.getCustomer() + "\n";
    }

    @Override
    protected String line(String label, long cents) {
        String money = String.format(Locale.ROOT, "%s%d.%02d", cents < 0 ? "-" : "", Math.abs(cents) / 100,
                Math.abs(cents) % 100);
        return String.format(Locale.ROOT, "  %-24s %9s\n", label, money);
    }

    @Override
    protected String footer(int grams) {
        return (grams > 0 ? "  shipping weight " + grams + " g" : "  nothing to ship") + "\n";
    }
}
