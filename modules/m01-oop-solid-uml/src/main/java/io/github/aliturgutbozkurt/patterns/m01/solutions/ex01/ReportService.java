package io.github.aliturgutbozkurt.patterns.m01.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.ReportFormat;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.ReportGenerator;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.Sale;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.SalesSummarizer;
import java.util.List;
import java.util.Objects;

/** Reference solution for assignment 01: coordinates; knows neither how to sum nor which format it renders. */
public class ReportService implements ReportGenerator {

    private final SalesSummarizer summarizer;
    private final ReportFormat format;

    public ReportService(SalesSummarizer summarizer, ReportFormat format) {
        this.summarizer = Objects.requireNonNull(summarizer, "summarizer");
        this.format = Objects.requireNonNull(format, "format");
    }

    @Override
    public String generate(List<Sale> sales) {
        return format.render(summarizer.summarize(sales));
    }
}
