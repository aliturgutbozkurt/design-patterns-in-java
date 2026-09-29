package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.classic;

import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The receiver: a sheet of text cells ({@code "A1"} → {@code "10"}). It knows how to change a cell, nothing about
 * undo. Not thread-safe.
 *
 * @see "m06 lesson, section Command"
 */
public final class Sheet {

    private final SortedMap<String, String> cells = new TreeMap<>();

    public Optional<String> get(String cell) {
        return Optional.ofNullable(cells.get(Objects.requireNonNull(cell, "cell")));
    }

    public void set(String cell, String value) {
        cells.put(Objects.requireNonNull(cell, "cell"), Objects.requireNonNull(value, "value"));
    }

    public void clear(String cell) {
        cells.remove(Objects.requireNonNull(cell, "cell"));
    }

    /** E.g. {@code "{A1=10, B1=20}"}, cells in name order. */
    @Override
    public String toString() {
        return cells.toString();
    }
}
