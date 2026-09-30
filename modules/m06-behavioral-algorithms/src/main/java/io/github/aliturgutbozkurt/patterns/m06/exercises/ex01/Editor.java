package io.github.aliturgutbozkurt.patterns.m06.exercises.ex01;

import java.util.List;
import java.util.function.Consumer;

/** GIVEN — do not modify. A text editor with undo/redo; every change goes through {@link #apply(Edit)}. */
public interface Editor {

    /** The current text. */
    String text();

    /**
     * Applies one edit and clears the redo history.
     *
     * @throws IndexOutOfBoundsException if the position or length does not fit the current text (nothing changes)
     */
    void apply(Edit edit);

    /** Undoes the most recent step; {@code false} if there is nothing to undo. */
    boolean undo();

    /** Re-applies the most recently undone step; {@code false} if there is nothing to redo. */
    boolean redo();

    boolean canUndo();

    boolean canRedo();

    /**
     * Runs {@code edits} with this editor; everything it applies becomes <em>one</em> undo step. Nested groups join the
     * outer group. If {@code edits} throws, the edits it made are rolled back and the exception is rethrown.
     */
    void group(Consumer<Editor> edits);

    /** The edits that can be undone, most recent first. */
    List<Edit> undoHistory();
}
