package io.github.aliturgutbozkurt.patterns.m01.exercises.ex01;

import java.util.List;

/** Assignment 01 — coordinates a summarizer and a format. No {@code if}/{@code switch} on the format in here. */
public class ReportService implements ReportGenerator {

    public ReportService(SalesSummarizer summarizer, ReportFormat format) {
        // TODO(ex01): keep both collaborators; reject null.
    }

    @Override
    public String generate(List<Sale> sales) {
        // TODO(ex01): summarize, then render.
        throw new UnsupportedOperationException("TODO(ex01): implement generate(List<Sale>)");
    }
}
