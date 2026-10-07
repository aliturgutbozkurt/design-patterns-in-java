package io.github.aliturgutbozkurt.patterns.capstone.reference.application.render;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.List;
import java.util.Optional;

/**
 * TEXT: the title, then fields joined with {@code " | "}, then the total.
 *
 * @see "capstone guide, Pattern map — Template Method"
 */
@PatternRole(value = DesignPattern.TEMPLATE_METHOD, role = "concrete class")
public final class TextRenderer extends ReportRenderer {

    private static final String SEPARATOR = " | ";

    @Override
    protected String heading(Table table) {
        return table.title();
    }

    @Override
    protected String row(List<String> fields) {
        return String.join(SEPARATOR, fields);
    }

    @Override
    protected Optional<String> total(List<String> fields) {
        return Optional.of(String.join(SEPARATOR, fields));
    }
}
