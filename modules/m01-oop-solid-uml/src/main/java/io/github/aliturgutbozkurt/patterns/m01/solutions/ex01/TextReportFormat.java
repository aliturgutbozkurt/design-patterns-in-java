package io.github.aliturgutbozkurt.patterns.m01.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.ReportFormat;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.SalesSummary;

/**
 * Reference solution for assignment 01: only the text layout.
 *
 * @see "m01 lesson, section SRP"
 */
public class TextReportFormat implements ReportFormat {

    @Override
    public String render(SalesSummary summary) {
        var out = new StringBuilder("SALES BY REGION\n");
        summary.totalsByRegion().forEach((region, total) -> out.append(row(region, total.toPlainString())));
        out.append(row("TOTAL", summary.grandTotal().toPlainString()));
        return out.toString();
    }

    private static String row(String label, String amount) {
        return "%-12s%10s".formatted(label, amount) + '\n';
    }
}
