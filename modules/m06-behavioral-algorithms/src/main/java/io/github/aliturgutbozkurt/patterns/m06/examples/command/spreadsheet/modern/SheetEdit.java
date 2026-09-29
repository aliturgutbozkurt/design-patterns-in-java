package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern;

import java.util.List;
import java.util.Objects;

/**
 * Commands as data: an edit is an immutable value that can be logged, compared and replayed. The behaviour lives in
 * {@link SheetEditor}, which switches over this sealed hierarchy.
 *
 * @see "m06 lesson, section Command"
 */
public sealed interface SheetEdit permits SheetEdit.SetCell, SheetEdit.ClearCell, SheetEdit.Batch {

    /**
     * Put {@code value} into {@code cell}.
     *
     * @see "m06 lesson, section Command"
     */
    record SetCell(String cell, String value) implements SheetEdit {
        public SetCell {
            Objects.requireNonNull(cell, "cell");
            Objects.requireNonNull(value, "value");
        }
    }

    /**
     * Empty {@code cell}.
     *
     * @see "m06 lesson, section Command"
     */
    record ClearCell(String cell) implements SheetEdit {
        public ClearCell {
            Objects.requireNonNull(cell, "cell");
        }
    }

    /**
     * A macro command: several edits applied, and undone, as one step.
     *
     * @see "m06 lesson, section Command"
     */
    record Batch(List<SheetEdit> edits) implements SheetEdit {
        public Batch {
            edits = List.copyOf(edits);
        }
    }
}
