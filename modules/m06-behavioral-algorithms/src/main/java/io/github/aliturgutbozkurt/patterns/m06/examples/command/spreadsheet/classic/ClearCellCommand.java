package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.classic;

import java.util.Objects;

/**
 * Concrete command: empties a cell and remembers its old value for undo.
 *
 * @see "m06 lesson, section Command"
 */
public final class ClearCellCommand implements Command {

    private final Sheet sheet;
    private final String cell;
    private String previous;  // null = the cell was already empty

    public ClearCellCommand(Sheet sheet, String cell) {
        this.sheet = Objects.requireNonNull(sheet, "sheet");
        this.cell = Objects.requireNonNull(cell, "cell");
    }

    @Override
    public void execute() {
        previous = sheet.get(cell).orElse(null);
        sheet.clear(cell);
    }

    @Override
    public void undo() {
        if (previous != null) {
            sheet.set(cell, previous);
        }
    }

    @Override
    public String label() {
        return "clear " + cell;
    }
}
