package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import java.util.List;
import java.util.Objects;

/**
 * Concrete class: the same skeleton as {@link CsvProductImporter}, only the {@code parse} step differs.
 *
 * @see "m06 lesson, section Template Method"
 */
public class JsonLinesProductImporter extends DataImporter {

    private final InMemoryProductStore store;

    public JsonLinesProductImporter(InMemoryProductStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    @Override
    protected List<RawRow> parse(String input) {
        return JsonLinesFormat.parse(input);
    }

    @Override
    protected void save(Product product) {
        store.save(product);
    }
}
