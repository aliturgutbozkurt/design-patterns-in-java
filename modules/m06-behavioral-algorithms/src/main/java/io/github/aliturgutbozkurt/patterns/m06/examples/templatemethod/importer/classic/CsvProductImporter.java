package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import java.util.List;
import java.util.Objects;

/**
 * Concrete class: supplies the CSV {@code parse} step and the {@code save} step; inherits the skeleton. Not final, so a
 * subclass can override the {@code validate} hook.
 *
 * @see "m06 lesson, section Template Method"
 */
public class CsvProductImporter extends DataImporter {

    private final InMemoryProductStore store;

    public CsvProductImporter(InMemoryProductStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    @Override
    protected List<RawRow> parse(String input) {
        return CsvFormat.parse(input);
    }

    @Override
    protected void save(Product product) {
        store.save(product);
    }
}
