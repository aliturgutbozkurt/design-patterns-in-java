package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic;

import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.HtmlFormatter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.Formatter;

/**
 * Creator subclass whose only job is to pick the Html product.
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class HtmlExporter extends DocumentExporter {

    @Override
    protected Formatter createFormatter() {
        return new HtmlFormatter();
    }
}
