package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export;

import java.util.List;
import java.util.Objects;

/**
 * A small table with a title — the product both exporters turn into text.
 *
 * @see "m02 lesson, section Factory Method"
 */
public record Report(String title, List<String> header, List<List<String>> rows) {

    public Report {
        Objects.requireNonNull(title, "title");
        header = List.copyOf(header);
        rows = rows.stream().map(List::copyOf).toList();
    }
}
