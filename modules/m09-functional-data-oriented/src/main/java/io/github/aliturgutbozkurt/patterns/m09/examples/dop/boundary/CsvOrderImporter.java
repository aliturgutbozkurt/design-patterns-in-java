package io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary;

import io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary.ImportedRow.Accepted;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary.ImportedRow.Rejected;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * The boundary: turns untrusted CSV text ({@code sku,quantity,unitPriceCents}) into typed rows. This is the only
 * place that deals with bad input; the value constructors do the checking, and their exceptions become
 * {@link Rejected} rows.
 *
 * @see "m09 lesson, section Data-oriented programming — parse, don't validate"
 */
public final class CsvOrderImporter {

    /** One row per line of {@code csv}, numbered from 1. Never throws for bad rows. */
    public List<ImportedRow> parse(String csv) {
        Objects.requireNonNull(csv, "csv");
        var lineNo = new AtomicInteger();
        return csv.lines().map(line -> parseRow(lineNo.incrementAndGet(), line)).toList();
    }

    /** The valid lines only, in order. */
    public static List<OrderLine> acceptedLines(List<ImportedRow> rows) {
        return rows.stream()
                .flatMap(row -> switch (row) {
                    case Accepted(_, var line) -> Stream.of(line);
                    case Rejected _ -> Stream.<OrderLine>empty();
                })
                .toList();
    }

    private static ImportedRow parseRow(int lineNo, String text) {
        String[] fields = text.strip().split(",", -1);
        if (fields.length != 3) {
            return new Rejected(lineNo,
                    "expected 3 fields (sku,quantity,unitPriceCents) but got " + fields.length);
        }
        try {
            var sku = new Sku(fields[0].strip());
            var quantity = new Quantity(number("quantity", fields[1]));
            long unitPrice = number("unitPriceCents", fields[2]);
            return new Accepted(lineNo, new OrderLine(sku, quantity, unitPrice));
        } catch (IllegalArgumentException e) {
            return new Rejected(lineNo, e.getMessage());
        }
    }

    private static int number(String field, String text) {
        try {
            return Integer.parseInt(text.strip());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " is not a number: \"" + text.strip() + "\"", e);
        }
    }
}
