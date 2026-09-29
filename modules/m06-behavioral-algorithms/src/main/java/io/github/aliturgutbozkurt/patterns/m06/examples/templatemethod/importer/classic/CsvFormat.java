package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses simple CSV: the first line is the header, cells are separated by commas, no quoting. A real project would
 * use a CSV library; this is enough to show the importer's steps.
 *
 * @see "m06 lesson, section Template Method"
 */
public final class CsvFormat {

    private CsvFormat() {}

    public static List<RawRow> parse(String input) {
        List<String> lines = input.lines().toList();
        List<RawRow> rows = new ArrayList<>();
        if (lines.isEmpty()) {
            return rows;
        }
        String[] header = lines.getFirst().split(",", -1);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            String[] cells = line.split(",", -1);
            Map<String, String> fields = new LinkedHashMap<>();
            for (int c = 0; c < Math.min(header.length, cells.length); c++) {
                fields.put(header[c].strip(), cells[c].strip());
            }
            rows.add(new RawRow(i + 1, fields));  // line numbers are 1-based and count the header
        }
        return rows;
    }
}
