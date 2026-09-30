package io.github.aliturgutbozkurt.patterns.m01.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.ReportFormat;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.SalesSummary;

/** Reference solution for assignment 01: only the CSV layout. */
public class CsvReportFormat implements ReportFormat {

    @Override
    public String render(SalesSummary summary) {
        var out = new StringBuilder("region,total\n");
        summary.totalsByRegion().forEach((region, total) -> out.append(region).append(',').append(total.toPlainString()).append('\n'));
        out.append("TOTAL,").append(summary.grandTotal().toPlainString()).append('\n');
        return out.toString();
    }
}
