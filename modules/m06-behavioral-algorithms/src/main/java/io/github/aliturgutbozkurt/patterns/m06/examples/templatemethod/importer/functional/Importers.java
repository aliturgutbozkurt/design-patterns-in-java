package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.functional;

import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.CsvFormat;
import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.JsonLinesFormat;
import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.Product;
import java.util.function.Consumer;

/**
 * Ready-made importers: each one is just a different set of step functions for the same {@link Importer}.
 *
 * @see "m06 lesson, section Template Method"
 */
public final class Importers {

    private Importers() {}

    public static Importer csv(Consumer<Product> sink) {
        return new Importer(CsvFormat::parse, _ -> true, sink);
    }

    public static Importer jsonLines(Consumer<Product> sink) {
        return new Importer(JsonLinesFormat::parse, _ -> true, sink);
    }
}
