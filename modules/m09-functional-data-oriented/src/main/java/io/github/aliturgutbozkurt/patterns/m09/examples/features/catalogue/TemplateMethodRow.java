package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Catalogue row: Template Method. An abstract class with a final algorithm and abstract steps became a
 * higher-order function that takes the steps as arguments.
 *
 * @see "m09 lesson, section Patterns that became language features"
 */
public final class TemplateMethodRow {

    static final List<String> LINES = List.of("mug", "tee");

    private TemplateMethodRow() {}

    /** Before: the skeleton is fixed in a base class; subclasses fill in the steps. */
    public static final class Classic {

        abstract static class Report {
            final String render(List<String> lines) {
                var out = new StringBuilder(header()).append('\n');
                for (String line : lines) {
                    out.append(line(line)).append('\n');
                }
                return out.append(footer()).toString();
            }

            abstract String header();

            abstract String line(String item);

            abstract String footer();
        }

        static final class StockReport extends Report {
            String header() { return "STOCK"; }

            String line(String item) { return "- " + item; }

            String footer() { return "end"; }
        }

        private Classic() {}

        public static String run() {
            return new StockReport().render(LINES);
        }
    }

    /** After: the skeleton is a function; the steps are its parameters. */
    public static final class Modern {

        private Modern() {}

        static String render(Supplier<String> header, Function<String, String> line, Supplier<String> footer,
                             List<String> lines) {
            var out = new StringBuilder(header.get()).append('\n');
            lines.forEach(item -> out.append(line.apply(item)).append('\n'));
            return out.append(footer.get()).toString();
        }

        public static String run() {
            return render(() -> "STOCK", item -> "- " + item, () -> "end", LINES);
        }
    }
}
