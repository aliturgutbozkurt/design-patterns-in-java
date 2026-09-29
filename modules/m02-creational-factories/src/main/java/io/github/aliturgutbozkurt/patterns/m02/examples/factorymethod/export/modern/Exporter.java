package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.modern;

import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.Formatter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.Report;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Factory Method without subclassing: the creation step is a {@link Supplier} passed in — often a constructor
 * reference such as {@code HtmlFormatter::new}.
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class Exporter {

    private final Supplier<? extends Formatter> formatterFactory;

    public Exporter(Supplier<? extends Formatter> formatterFactory) {
        this.formatterFactory = Objects.requireNonNull(formatterFactory, "formatterFactory");
    }

    public String export(Report report) {
        Formatter formatter = formatterFactory.get();
        var out = new StringBuilder(formatter.begin(report.title()));
        out.append(formatter.header(report.header()));
        for (List<String> row : report.rows()) {
            out.append(formatter.row(row));
        }
        return out.append(formatter.end()).toString();
    }
}
