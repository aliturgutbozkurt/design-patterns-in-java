package io.github.aliturgutbozkurt.patterns.m11.examples.di.container;

import java.util.Objects;

/**
 * The top of the sample graph: {@code ReportService → ReportRepository, ReportFormatter → Clock}. It knows nothing
 * about the container — plain constructor injection works with or without one.
 *
 * @see "m11 lesson, section Dependency Injection — how a container works"
 */
public final class ReportService {

    private final ReportRepository repository;
    private final ReportFormatter formatter;

    public ReportService(ReportRepository repository, ReportFormatter formatter) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
    }

    /** Today's sales report. */
    public String dailyReport() {
        return formatter.format(repository.unitsSold());
    }
}
