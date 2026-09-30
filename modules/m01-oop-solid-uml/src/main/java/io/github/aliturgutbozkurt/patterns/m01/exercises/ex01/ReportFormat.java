package io.github.aliturgutbozkurt.patterns.m01.exercises.ex01;

/** GIVEN — do not modify. Renders a summary as text; a new format is a new implementation (or lambda). */
@FunctionalInterface
public interface ReportFormat {

    String render(SalesSummary summary);
}
