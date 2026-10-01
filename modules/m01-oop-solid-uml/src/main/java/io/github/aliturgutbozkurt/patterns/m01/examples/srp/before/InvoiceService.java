package io.github.aliturgutbozkurt.patterns.m01.examples.srp.before;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * SRP violation: a "god class" that calculates, formats, stores and e-mails invoices. Each of those is a separate
 * reason to change — a new tax rule, a new layout, a database, a mail provider all edit this one class.
 *
 * @see "m01 lesson, section SRP"
 */
public final class InvoiceService {

    /**
     * One invoice line: {@code quantity × unitPrice}.
     *
     * @see "m01 lesson, section SRP"
     */
    public record Line(String product, int quantity, BigDecimal unitPrice) {}

    private final Map<String, String> sentInvoices = new LinkedHashMap<>();

    /** Calculates, formats, stores and "sends" the invoice; returns its text. */
    public String process(String number, String customer, List<Line> lines) {
        Objects.requireNonNull(number, "number");
        Objects.requireNonNull(customer, "customer");
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("an invoice needs at least one line");
        }
        // 1) calculation
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Line line : lines) {
            subtotal = subtotal.add(line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())));
        }
        subtotal = subtotal.setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal vat = subtotal.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal total = subtotal.add(vat);
        // 2) formatting
        var text = new StringBuilder();
        text.append("INVOICE ").append(number).append('\n');
        text.append("Customer: ").append(customer).append('\n');
        for (Line line : lines) {
            BigDecimal amount = line.unitPrice().multiply(BigDecimal.valueOf(line.quantity()))
                    .setScale(2, RoundingMode.HALF_EVEN);
            text.append("  %s x %-12s%10s%10s".formatted(line.quantity(), line.product(),
                    line.unitPrice().setScale(2, RoundingMode.HALF_EVEN).toPlainString(), amount.toPlainString()))
                    .append('\n');
        }
        text.append("%-9s%10s".formatted("Subtotal:", subtotal.toPlainString())).append('\n');
        text.append("%-9s%10s".formatted("VAT 20%:", vat.toPlainString())).append('\n');
        text.append("%-9s%10s".formatted("Total:", total.toPlainString())).append('\n');
        String result = text.toString();
        // 3) storage
        sentInvoices.put(number, result);
        // 4) delivery
        System.out.println("Emailing invoice " + number + " to " + customer);
        return result;
    }

    /** Looks up the text of an invoice processed earlier. */
    public Optional<String> find(String number) {
        return Optional.ofNullable(sentInvoices.get(number));
    }
}
