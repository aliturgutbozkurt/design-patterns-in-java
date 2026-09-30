package io.github.aliturgutbozkurt.patterns.m01.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.Ex01Contract;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.ReportFormat;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.ReportGenerator;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.SalesSummarizer;

class Ex01SolutionTest extends Ex01Contract {

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
