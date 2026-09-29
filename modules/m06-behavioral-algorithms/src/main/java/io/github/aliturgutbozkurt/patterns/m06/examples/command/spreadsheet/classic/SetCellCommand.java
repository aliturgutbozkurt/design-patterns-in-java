package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.classic;

import java.util.Objects;

/**
 * Concrete command: writes a value into a cell and remembers what was there before (possibly nothing), so it can undo.
 *
 * @see "m06 lesson, section Command"
 */
public final class SetCellCommand implements Command {

    private final Sheet sheet;
    private final String cell;
    private final String value;
    private String previous;  // captured by execute(), used by undo(); null = the cell was empty

    public SetCellCommand(Sheet sheet, String cell, String value) {
        this.sheet = Objects.requireNonNull(sheet, "sheet");
        this.cell = Objects.requireNonNull(cell, "cell");
        this.value = Objects.requireNonNull(value, "value");
    }

    @Override
    public void execute() {
        previous = sheet.get(cell).orElse(null);
        sheet.set(cell, value);
    }

    @Override
    public void undo() {
        if (previous == null) {
            sheet.clear(cell);
        } else {
            sheet.set(cell, previous);
        }
    }

    @Override
    public String label() {
        return "set " + cell + "=" + value;
    }
}
