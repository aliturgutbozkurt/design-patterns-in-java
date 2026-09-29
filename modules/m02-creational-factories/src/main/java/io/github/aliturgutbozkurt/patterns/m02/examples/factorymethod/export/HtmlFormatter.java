package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HTML table; cell text is escaped.
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class HtmlFormatter implements Formatter {

    @Override
    public String begin(String title) {
        return "<h1>" + escape(title) + "</h1>\n<table>\n";
    }

    @Override
    public String header(List<String> cells) {
        return tableRow("th", cells);
    }

    @Override
    public String row(List<String> cells) {
        return tableRow("td", cells);
    }

    @Override
    public String end() {
        return "</table>\n";
    }

    private static String tableRow(String tag, List<String> cells) {
        return cells.stream()
                .map(cell -> "<" + tag + ">" + escape(cell) + "</" + tag + ">")
                .collect(Collectors.joining("", "  <tr>", "</tr>\n"));
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
