package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.modern;

import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.CsvFormatter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.Formatter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.HtmlFormatter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.MarkdownFormatter;
import java.util.function.Supplier;

/**
 * A registry of factories keyed by an enum: each constant carries a constructor reference, so there is one line per
 * format instead of one subclass per format.
 *
 * @see "m02 lesson, section Factory Method"
 */
public enum ExportFormat {
    MARKDOWN(MarkdownFormatter::new),
    HTML(HtmlFormatter::new),
    CSV(CsvFormatter::new);

    private final Supplier<Formatter> factory;

    ExportFormat(Supplier<Formatter> factory) {
        this.factory = factory;
    }

    public Exporter exporter() {
        return new Exporter(factory);
    }
}
