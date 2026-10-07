package io.github.aliturgutbozkurt.patterns.capstone.api.report;

/**
 * GIVEN — do not modify. Inbound port of feature F10: run a report and render it.
 *
 * @see "capstone brief §2.2 — Reports (F10); SPEC-capstone, Output formats"
 */
public interface ReportUseCase {

    /** Computes the report the request asks for from the current state of the shop. */
    Report run(ReportRequest request);

    /** Renders a report as {@code TEXT} or {@code CSV}; every line ends with {@code \n}. */
    String render(Report report, ReportFormat format);
}
