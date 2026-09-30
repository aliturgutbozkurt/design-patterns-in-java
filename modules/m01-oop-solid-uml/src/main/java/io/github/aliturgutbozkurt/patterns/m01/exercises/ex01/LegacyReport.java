package io.github.aliturgutbozkurt.patterns.m01.exercises.ex01;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * GIVEN — do not modify. The god class you are replacing: it sums, sorts and formats in one method and picks the
 * format with a {@code String} flag. Its output is the reference your refactoring must reproduce exactly.
 */
public final class LegacyReport {

    public String generate(List<Sale> sales, String format) {
        Map<String, BigDecimal> totals = new TreeMap<>();
        BigDecimal grandTotal = new BigDecimal("0.00");
        for (Sale sale : sales) {
            totals.merge(sale.region(), sale.amount(), BigDecimal::add);
            grandTotal = grandTotal.add(sale.amount());
        }
        var out = new StringBuilder();
        if (format.equals("text")) {
            out.append("SALES BY REGION\n");
            for (var entry : totals.entrySet()) {
                out.append("%-12s%10s".formatted(entry.getKey(), entry.getValue().toPlainString())).append('\n');
            }
            out.append("%-12s%10s".formatted("TOTAL", grandTotal.toPlainString())).append('\n');
        } else if (format.equals("csv")) {
            out.append("region,total\n");
            for (var entry : totals.entrySet()) {
                out.append(entry.getKey()).append(',').append(entry.getValue().toPlainString()).append('\n');
            }
            out.append("TOTAL,").append(grandTotal.toPlainString()).append('\n');
        } else {
            throw new IllegalArgumentException("unknown format: " + format);
        }
        return out.toString();
    }
}
