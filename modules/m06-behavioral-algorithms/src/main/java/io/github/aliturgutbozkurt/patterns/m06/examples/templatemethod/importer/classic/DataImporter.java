package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.ImportReport.Rejection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classic Template Method: {@link #importData(String)} fixes the skeleton parse → validate → save; subclasses supply
 * the format-specific steps and may override the hooks.
 *
 * @see "m06 lesson, section Template Method"
 */
public abstract class DataImporter {

    /** The template method. It is {@code final}, so no subclass can reorder or skip the steps. */
    public final ImportReport importData(String input) {
        Objects.requireNonNull(input, "input");
        List<String> imported = new ArrayList<>();
        List<Rejection> rejected = new ArrayList<>();
        for (RawRow row : parse(input)) {
            Product product;
            try {
                product = row.toProduct();
            } catch (IllegalArgumentException e) {
                reject(new Rejection(row.line(), e.getMessage()), rejected);
                continue;
            }
            if (!validate(product)) {
                reject(new Rejection(row.line(), "failed validation"), rejected);
                continue;
            }
            save(product);
            imported.add(product.sku());
        }
        return new ImportReport(imported, rejected);
    }

    private void reject(Rejection rejection, List<Rejection> rejected) {
        rejected.add(rejection);
        onError(rejection);
    }

    /** Primitive step: split the input into rows of text fields. A syntax error aborts the whole import. */
    protected abstract List<RawRow> parse(String input);

    /** Hook: extra business rules. The default accepts every product. */
    protected boolean validate(Product product) {
        return true;
    }

    /** Primitive step: store one valid product. */
    protected abstract void save(Product product);

    /** Hook: called for every skipped row, e.g. to log it. The default does nothing; the row is still reported. */
    protected void onError(Rejection rejection) {}
}
