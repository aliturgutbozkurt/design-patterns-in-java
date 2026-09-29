package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The outcome of one import: the SKUs that were saved and the rows that were skipped, with their line numbers.
 *
 * @see "m06 lesson, section Template Method"
 */
public record ImportReport(List<String> importedSkus, List<Rejection> rejected) {

    public ImportReport {
        importedSkus = List.copyOf(importedSkus);
        rejected = List.copyOf(rejected);
    }

    /** E.g. {@code "imported [A-1]; rejected [line 3: missing field: price]"}. */
    public String summary() {
        return "imported " + importedSkus + "; rejected "
                + rejected.stream().map(r -> "line " + r.line() + ": " + r.reason())
                        .collect(Collectors.joining(", ", "[", "]"));
    }

    /**
     * A skipped row and the reason it was skipped.
     *
     * @see "m06 lesson, section Template Method"
     */
    public record Rejection(int line, String reason) {}
}
