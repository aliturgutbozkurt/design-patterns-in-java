package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.functional;

import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.ImportReport;
import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.ImportReport.Rejection;
import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.Product;
import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.RawRow;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Template Method as a higher-order function: the skeleton lives in {@link #importData(String)}, and the steps are
 * functions passed in, not methods inherited.
 *
 * @see "m06 lesson, section Template Method"
 */
public record Importer(Function<String, List<RawRow>> parser, Predicate<Product> validator, Consumer<Product> sink) {

    public Importer {
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(validator, "validator");
        Objects.requireNonNull(sink, "sink");
    }

    /** The same skeleton as {@code DataImporter.importData}: parse → validate → save. */
    public ImportReport importData(String input) {
        Objects.requireNonNull(input, "input");
        List<String> imported = new ArrayList<>();
        List<Rejection> rejected = new ArrayList<>();
        for (RawRow row : parser.apply(input)) {
            Product product;
            try {
                product = row.toProduct();
            } catch (IllegalArgumentException e) {
                rejected.add(new Rejection(row.line(), e.getMessage()));
                continue;
            }
            if (!validator.test(product)) {
                rejected.add(new Rejection(row.line(), "failed validation"));
                continue;
            }
            sink.accept(product);
            imported.add(product.sku());
        }
        return new ImportReport(imported, rejected);
    }

    /** A copy with a different validation step; everything else stays the same. */
    public Importer withValidator(Predicate<Product> newValidator) {
        return new Importer(parser, newValidator, sink);
    }
}
