package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.functional;

import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/templatemethod/importer/functional/ImporterDemo.java}
 *
 * @see "m06 lesson, section Template Method"
 */
public final class ImporterDemo {

    static final String CSV = """
            sku,name,price
            A-1,Pencil,1.20
            A-2,Notebook,abc
            A-3,Eraser,0.00
            A-4,Ruler
            A-5,Stapler,7.50
            """;

    static final String JSON_LINES = """
            {"sku":"A-1","name":"Pencil","price":1.20}
            {"sku":"A-2","name":"Notebook","price":"abc"}
            {"sku":"A-3","name":"Eraser","price":0.00}
            {"sku":"A-4","name":"Ruler"}
            {"sku":"A-5","name":"Stapler","price":7.50}
            """;

    private ImporterDemo() {}

    public static void main(String[] args) {
        List<Product> saved = new ArrayList<>();
        Importer csv = Importers.csv(saved::add);
        System.out.println("CSV:        " + csv.importData(CSV).summary());
        System.out.println("JSON Lines: " + Importers.jsonLines(_ -> {}).importData(JSON_LINES).summary());

        // Changing one step the functional way: pass another function, no subclass.
        Importer strict = Importers.csv(_ -> {}).withValidator(product -> product.price().signum() > 0);
        System.out.println("strict CSV: " + strict.importData(CSV).summary());

        System.out.println("saved by the sink: " + saved.stream().map(Product::sku).toList());
    }
}
