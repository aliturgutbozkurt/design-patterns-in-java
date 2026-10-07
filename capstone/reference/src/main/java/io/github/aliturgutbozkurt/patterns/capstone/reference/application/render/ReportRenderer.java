package io.github.aliturgutbozkurt.patterns.capstone.reference.application.render;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report;
import java.util.List;
import java.util.Optional;

/**
 * Renders a report in a fixed order — heading line, one line per row, total line — while subclasses decide what each
 * line looks like. Every line ends with {@code \n}.
 *
 * @see "capstone guide, Pattern map — Template Method"
 */
@PatternRole(value = DesignPattern.TEMPLATE_METHOD, role = "abstract class (template method render)")
public abstract class ReportRenderer {

    /** The template method: the order of the parts is fixed here, once. */
    public final String render(Report report) {
        Table table = Table.of(report);
        StringBuilder out = new StringBuilder(heading(table)).append('\n');
        for (List<String> row : table.rows()) {
            out.append(row(row)).append('\n');
        }
        if (!table.total().isEmpty()) {
            total(table.total()).ifPresent(line -> out.append(line).append('\n'));
        }
        return out.toString();
    }

    /** The first line (a title or a header). */
    protected abstract String heading(Table table);

    /** One row. */
    protected abstract String row(List<String> fields);

    /** The total line, or empty when this format shows no totals. */
    protected abstract Optional<String> total(List<String> fields);
}
