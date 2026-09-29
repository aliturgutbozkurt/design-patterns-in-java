package io.github.aliturgutbozkurt.patterns.m01.exercises.ex01;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m01-oop-solid-uml test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected SalesSummarizer summarizer() {
        return new RegionSummarizer();
    }

    @Override
    protected ReportFormat textFormat() {
        return new TextReportFormat();
    }

    @Override
    protected ReportFormat csvFormat() {
        return new CsvReportFormat();
    }

    @Override
    protected ReportGenerator generator(SalesSummarizer summarizer, ReportFormat format) {
        return new ReportService(summarizer, format);
    }
}
