package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic;

import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.Formatter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.Report;
import java.util.List;

/**
 * Classic Factory Method: the creator's algorithm ({@link #export}) is written once and asks the abstract
 * {@link #createFormatter()} hook for its product; each subclass decides <em>which</em> product.
 *
 * @see "m02 lesson, section Factory Method"
 */
public abstract class DocumentExporter {

    /** The factory method. */
    protected abstract Formatter createFormatter();

    public final String export(Report report) {
        Formatter formatter = createFormatter();
        var out = new StringBuilder(formatter.begin(report.title()));
        out.append(formatter.header(report.header()));
        for (List<String> row : report.rows()) {
            out.append(formatter.row(row));
        }
        return out.append(formatter.end()).toString();
    }
}
