package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

import java.util.Locale;

/**
 * Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/features/catalogue/CatalogueDemo.java}
 *
 * @see "m09 lesson, section Patterns that became language features"
 */
public final class CatalogueDemo {

    private CatalogueDemo() {}

    public static void main(String[] args) {
        System.out.println("-- classic and modern side by side (same input, same output)");
        for (Catalogue.Row row : Catalogue.rows()) {
            String classic = row.classic().get();
            String modern = row.modern().get();
            System.out.println(String.format(Locale.ROOT, "%-15s %s %s", row.pattern(),
                    classic.equals(modern) ? "same:" : "DIFFERENT:", modern.replace('\n', '|')));
        }
        System.out.println("-- the table");
        System.out.print(Catalogue.markdownTable());
    }
}
