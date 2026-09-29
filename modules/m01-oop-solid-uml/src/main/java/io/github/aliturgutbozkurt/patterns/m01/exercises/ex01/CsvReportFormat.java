package io.github.aliturgutbozkurt.patterns.m01.exercises.ex01;

/** Assignment 01 — the CSV layout, identical to {@link LegacyReport}'s {@code "csv"} output. */
public class CsvReportFormat implements ReportFormat {

    @Override
    public String render(SalesSummary summary) {
        // TODO(ex01): only the layout lives here — no summing.
        throw new UnsupportedOperationException("TODO(ex01): implement render(SalesSummary)");
    }
}
