package io.github.aliturgutbozkurt.patterns.capstone.reference.application.render;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CSV: the column names as header, one row per entry, no title and no total; fields with a comma or a quote are
 * quoted RFC-4180 style.
 *
 * @see "capstone guide, Pattern map — Template Method"
 */
@PatternRole(value = DesignPattern.TEMPLATE_METHOD, role = "concrete class")
public final class CsvRenderer extends ReportRenderer {

    @Override
    protected String heading(Table table) {
        return row(table.columns());
    }

    @Override
    protected String row(List<String> fields) {
        return fields.stream().map(CsvRenderer::quote).collect(Collectors.joining(","));
    }

    @Override
    protected Optional<String> total(List<String> fields) {
        return Optional.empty();
    }

    private static String quote(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return '"' + field.replace("\"", "\"\"") + '"';
        }
        return field;
    }
}
