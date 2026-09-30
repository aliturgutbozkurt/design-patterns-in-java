package io.github.aliturgutbozkurt.patterns.m07.examples.memento.editor;

import java.util.List;
import java.util.Objects;

/**
 * Originator: a tiny text editor whose whole state fits in an {@link EditorSnapshot}. Every edit records the state
 * before it, so undo and redo are just "restore another snapshot".
 *
 * @see "m07 lesson, section Memento — Modern Java 27"
 */
public final class Editor {

    private final UndoHistory history;
    private String text = "";
    private int cursor;
    private int selectionStart;
    private int selectionEnd;

    public Editor(int historyCapacity) {
        this.history = new UndoHistory(historyCapacity);
    }

    /** Replaces the selection (or inserts at the cursor) with {@code input}. */
    public void type(String input) {
        Objects.requireNonNull(input, "input");
        history.record(snapshot());
        text = text.substring(0, selectionStart) + input + text.substring(selectionEnd);
        placeCursor(selectionStart + input.length());
    }

    /** Selects {@code text[start, end)}; the cursor moves to {@code end}. */
    public void select(int start, int end) {
        var selected = new EditorSnapshot(text, end, start, end); // validates the range
        history.record(snapshot());
        restore(selected);
    }

    public void moveCursor(int position) {
        var moved = new EditorSnapshot(text, position, position, position); // validates the position
        history.record(snapshot());
        restore(moved);
    }

    /** Goes back one step; returns {@code false} (and changes nothing) if there is nothing to undo. */
    public boolean undo() {
        return history.undo(snapshot()).map(this::restoreAndConfirm).orElse(false);
    }

    /** Goes forward one step; returns {@code false} (and changes nothing) if there is nothing to redo. */
    public boolean redo() {
        return history.redo(snapshot()).map(this::restoreAndConfirm).orElse(false);
    }

    public EditorSnapshot snapshot() {
        return new EditorSnapshot(text, cursor, selectionStart, selectionEnd);
    }

    /** Undo snapshots, newest first. */
    public List<EditorSnapshot> history() {
        return history.history();
    }

    public String render() {
        return snapshot().render();
    }

    private boolean restoreAndConfirm(EditorSnapshot snapshot) {
        restore(snapshot);
        return true;
    }

    private void restore(EditorSnapshot snapshot) {
        text = snapshot.text();
        cursor = snapshot.cursor();
        selectionStart = snapshot.selectionStart();
        selectionEnd = snapshot.selectionEnd();
    }

    private void placeCursor(int position) {
        cursor = position;
        selectionStart = position;
        selectionEnd = position;
    }
}
