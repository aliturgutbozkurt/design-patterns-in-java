package io.github.aliturgutbozkurt.patterns.m06.exercises.ex01;

import java.util.List;
import java.util.function.Consumer;

/**
 * Assignment 01 — your Command-based editor. Turn every {@link Edit} into a command object that can execute and undo
 * itself (one class per edit type, plus one for a group), and keep undo/redo stacks of those commands.
 */
public class CommandEditor implements Editor {

    /** @param maxHistory how many undo steps are kept (at least 1); older steps are dropped */
    public CommandEditor(String initialText, int maxHistory) {
        // TODO(ex01): check the arguments and store the text (a StringBuilder is handy) and the history limit.
    }

    @Override
    public String text() {
        throw new UnsupportedOperationException("TODO(ex01): implement text()");
    }

    @Override
    public void apply(Edit edit) {
        // TODO(ex01): check bounds, create the matching command, execute it, push it, clear redo.
        throw new UnsupportedOperationException("TODO(ex01): implement apply(Edit)");
    }

    @Override
    public boolean undo() {
        throw new UnsupportedOperationException("TODO(ex01): implement undo()");
    }

    @Override
    public boolean redo() {
        throw new UnsupportedOperationException("TODO(ex01): implement redo()");
    }

    @Override
    public boolean canUndo() {
        throw new UnsupportedOperationException("TODO(ex01): implement canUndo()");
    }

    @Override
    public boolean canRedo() {
        throw new UnsupportedOperationException("TODO(ex01): implement canRedo()");
    }

    @Override
    public void group(Consumer<Editor> edits) {
        // TODO(ex01): collect the commands made inside into one macro command; roll back if edits throws.
        throw new UnsupportedOperationException("TODO(ex01): implement group(Consumer)");
    }

    @Override
    public List<Edit> undoHistory() {
        throw new UnsupportedOperationException("TODO(ex01): implement undoHistory()");
    }
}
