package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export;

import java.util.Collections;
import java.util.List;

/**
 * Markdown table.
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class MarkdownFormatter implements Formatter {

    @Override
    public String begin(String title) {
        return "# " + title + "\n\n";
    }

    @Override
    public String header(List<String> cells) {
        return row(cells) + row(Collections.nCopies(cells.size(), "---"));
    }

    @Override
    public String row(List<String> cells) {
        return "| " + String.join(" | ", cells) + " |\n";
    }

    @Override
    public String end() {
        return "";
    }
}
