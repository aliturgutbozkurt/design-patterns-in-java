package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The "patterns that became language features" table as data. Each row points to the compiled before/after code and
 * to the lesson with the full treatment; the lesson's table is this class's {@link #markdownTable()} output.
 *
 * @see "m09 lesson, section Patterns that became language features"
 */
public final class Catalogue {

    /** One pattern: what replaced it, when the class-based form is still worth writing, and runnable sides. */
    public record Row(String pattern, String modernFeature, String stillUseTheClassWhen, String lesson,
                      Supplier<String> classic, Supplier<String> modern) {
        public Row {
            Objects.requireNonNull(pattern, "pattern");
            Objects.requireNonNull(modernFeature, "modernFeature");
            Objects.requireNonNull(stillUseTheClassWhen, "stillUseTheClassWhen");
            Objects.requireNonNull(lesson, "lesson");
            Objects.requireNonNull(classic, "classic");
            Objects.requireNonNull(modern, "modern");
        }
    }

    private Catalogue() {}

    public static List<Row> rows() {
        return List.of(
                new Row("Strategy", "lambda / `Comparator`", "the strategy has state, several methods or a name in the domain",
                        "m06", StrategyRow.Classic::run, StrategyRow.Modern::run),
                new Row("Command", "sealed records + `switch`, `Runnable`", "commands carry their own undo logic or are plugged in from outside",
                        "m06", CommandRow.Classic::run, CommandRow.Modern::run),
                new Row("Template Method", "higher-order function", "the steps share protected state or there are many of them",
                        "m06", TemplateMethodRow.Classic::run, TemplateMethodRow.Modern::run),
                new Row("Visitor", "sealed records + `switch`", "the hierarchy is open and must be extended from outside the module",
                        "m08", VisitorRow.Classic::run, VisitorRow.Modern::run),
                new Row("Iterator", "`Stream.iterate` + gatherers", "the cursor walks an external resource or must be paused and resumed",
                        "m06", IteratorRow.Classic::run, IteratorRow.Modern::run),
                new Row("Factory", "`Map<String, Supplier<T>>` + constructor refs", "creation needs several steps, parameters or its own dependencies",
                        "m02", FactoryRow.Classic::run, FactoryRow.Modern::run),
                new Row("Decorator", "`Function.andThen`", "the wrapped type has many methods (e.g. `InputStream`)",
                        "m04", DecoratorRow.Classic::run, DecoratorRow.Modern::run),
                new Row("Singleton", "`enum` with one constant", "never; prefer passing the object in (dependency injection)",
                        "m02", SingletonRow.Classic::run, SingletonRow.Modern::run));
    }

    /** The comparison table quoted in the lesson. */
    public static String markdownTable() {
        var out = new StringBuilder("| Pattern | Java feature that does its job | Still write the class when… | Full treatment |\n")
                .append("|---|---|---|---|\n");
        for (Row row : rows()) {
            out.append("| ").append(row.pattern()).append(" | ").append(row.modernFeature()).append(" | ")
                    .append(row.stillUseTheClassWhen()).append(" | ").append(row.lesson()).append(" |\n");
        }
        return out.toString();
    }
}
