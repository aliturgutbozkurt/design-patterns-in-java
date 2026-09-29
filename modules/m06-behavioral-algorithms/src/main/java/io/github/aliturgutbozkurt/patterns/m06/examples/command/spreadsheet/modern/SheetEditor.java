package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern;

import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.classic.Sheet;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern.SheetEdit.Batch;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern.SheetEdit.ClearCell;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern.SheetEdit.SetCell;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Applies {@link SheetEdit}s to a {@link Sheet}. Applying an edit returns its <em>inverse</em>, another edit; undo is
 * "apply the inverse". Every applied edit is logged, so the log can be replayed. Not thread-safe.
 *
 * @see "m06 lesson, section Command"
 */
public final class SheetEditor {

    private final Sheet sheet;
    private final Deque<SheetEdit> undoStack = new ArrayDeque<>();
    private final List<SheetEdit> log = new ArrayList<>();

    public SheetEditor(Sheet sheet) {
        this.sheet = Objects.requireNonNull(sheet, "sheet");
    }

    /** Applies {@code edit}, logs it and returns the edit that undoes it. */
    public SheetEdit apply(SheetEdit edit) {
        Objects.requireNonNull(edit, "edit");
        log.add(edit);
        return applyAndInvert(edit);
    }

    /** No {@code default}: the compiler checks that every kind of edit is handled. */
    private SheetEdit applyAndInvert(SheetEdit edit) {
        return switch (edit) {
            case SetCell(String cell, String value) -> {
                SheetEdit inverse = restore(cell);
                sheet.set(cell, value);
                yield inverse;
            }
            case ClearCell(String cell) -> {
                SheetEdit inverse = restore(cell);
                sheet.clear(cell);
                yield inverse;
            }
            case Batch(List<SheetEdit> edits) -> {
                List<SheetEdit> inverses = new ArrayList<>();
                for (SheetEdit step : edits) {
                    inverses.addFirst(applyAndInvert(step));  // undo runs the steps in reverse order
                }
                yield new Batch(inverses);
            }
        };
    }

    /** The edit that brings {@code cell} back to its current state. */
    private SheetEdit restore(String cell) {
        return sheet.get(cell).<SheetEdit>map(old -> new SetCell(cell, old)).orElse(new ClearCell(cell));
    }

    /** Applies {@code edit}, remembers its inverse for {@link #undo()} and returns that inverse. */
    public SheetEdit perform(SheetEdit edit) {
        SheetEdit inverse = apply(edit);
        undoStack.push(inverse);
        return inverse;
    }

    /** Applies the inverse of the last performed edit; {@code false} if there is nothing to undo. */
    public boolean undo() {
        SheetEdit inverse = undoStack.poll();
        if (inverse == null) {
            return false;
        }
        apply(inverse);
        return true;
    }

    /** Every edit applied so far, oldest first (undos appear as their inverse edits). */
    public List<SheetEdit> log() {
        return List.copyOf(log);
    }

    /** Builds a new sheet by applying {@code edits} in order to an empty one. */
    public static Sheet replay(List<SheetEdit> edits) {
        var editor = new SheetEditor(new Sheet());
        edits.forEach(editor::apply);
        return editor.sheet;
    }
}
