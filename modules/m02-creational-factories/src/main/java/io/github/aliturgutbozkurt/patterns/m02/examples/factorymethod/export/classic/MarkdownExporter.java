package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic;

import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.MarkdownFormatter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.Formatter;

/**
 * Creator subclass whose only job is to pick the Markdown product.
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class MarkdownExporter extends DocumentExporter {

    @Override
    protected Formatter createFormatter() {
        return new MarkdownFormatter();
    }
}
