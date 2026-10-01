package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import java.util.stream.Collectors;

/**
 * Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/templatemethod/importer/classic/ImporterDemo.java}
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
        var store = new InMemoryProductStore();
        System.out.println("CSV:        " + new CsvProductImporter(store).importData(CSV).summary());
        System.out.println("JSON Lines: " + new JsonLinesProductImporter(new InMemoryProductStore())
                .importData(JSON_LINES).summary());

        // Changing one step the classic way: a subclass (here anonymous) that overrides the hook.
        DataImporter strict = new CsvProductImporter(new InMemoryProductStore()) {
            @Override
            protected boolean validate(Product product) {
                return product.price().signum() > 0;
            }
        };
        System.out.println("strict CSV: " + strict.importData(CSV).summary());

        System.out.println("store: " + store.all().stream()
                .map(p -> p.sku() + " " + p.name() + " " + p.price())
                .collect(Collectors.joining(", ")));
    }
}
