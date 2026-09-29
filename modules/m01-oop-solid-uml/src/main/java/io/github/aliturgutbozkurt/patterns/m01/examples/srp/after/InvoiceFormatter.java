package io.github.aliturgutbozkurt.patterns.m01.examples.srp.after;

/**
 * Responsibility: the text layout. Changes only when the layout changes.
 *
 * @see "m01 lesson, section SRP"
 */
public final class InvoiceFormatter {

    public String format(Invoice invoice, InvoiceTotals totals) {
        var text = new StringBuilder();
        text.append("INVOICE ").append(invoice.number()).append('\n');
        text.append("Customer: ").append(invoice.customer()).append('\n');
        for (InvoiceLine line : invoice.lines()) {
            text.append("  %s x %-12s%10s%10s".formatted(line.quantity(), line.product(),
                    line.unitPrice().toPlainString(), line.amount().toPlainString())).append('\n');
        }
        text.append(row("Subtotal:", totals.subtotal().toPlainString()));
        text.append(row("VAT 20%:", totals.vat().toPlainString()));
        text.append(row("Total:", totals.total().toPlainString()));
        return text.toString();
    }

    private static String row(String label, String value) {
        return "%-9s%10s".formatted(label, value) + '\n';
    }
}
