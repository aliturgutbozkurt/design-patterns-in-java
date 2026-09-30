package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CSV (RFC 4180 style): cells containing a comma, quote or line break are quoted, quotes are doubled. No title line.
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class CsvFormatter implements Formatter {

    @Override
    public String begin(String title) {
        return "";
    }

    @Override
    public String header(List<String> cells) {
        return row(cells);
    }

    @Override
    public String row(List<String> cells) {
        return cells.stream().map(CsvFormatter::quote).collect(Collectors.joining(",", "", "\n"));
    }

    @Override
    public String end() {
        return "";
    }

    private static String quote(String cell) {
        if (cell.contains(",") || cell.contains("\"") || cell.contains("\n")) {
            return "\"" + cell.replace("\"", "\"\"") + "\"";
        }
        return cell;
    }
}
