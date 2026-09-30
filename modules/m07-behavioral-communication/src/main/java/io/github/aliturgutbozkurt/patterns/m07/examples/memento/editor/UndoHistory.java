package io.github.aliturgutbozkurt.patterns.m07.examples.memento.editor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Caretaker with a bounded undo stack and a redo stack, written with sequenced-collection methods: the newest
 * snapshot is last ({@code addLast}/{@code removeLast}/{@code getLast}), the oldest one is dropped with
 * {@code removeFirst}, and {@code reversed()} gives a newest-first view.
 *
 * @see "m07 lesson, section Memento — Modern Java 27"
 */
public final class UndoHistory {

    private final int capacity;
    private final Deque<EditorSnapshot> undo = new ArrayDeque<>();
    private final Deque<EditorSnapshot> redo = new ArrayDeque<>();

    public UndoHistory(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
    }

    /** Remembers the state before a new edit; a new edit makes the redo stack meaningless. */
    public void record(EditorSnapshot before) {
        pushUndo(Objects.requireNonNull(before, "before"));
        redo.clear();
    }

    /** The snapshot to go back to, if any; {@code current} becomes redoable. */
    public Optional<EditorSnapshot> undo(EditorSnapshot current) {
        if (undo.isEmpty()) {
            return Optional.empty();
        }
        redo.addLast(current);
        return Optional.of(undo.removeLast());
    }

    /** The snapshot to go forward to, if any; {@code current} becomes undoable again. */
    public Optional<EditorSnapshot> redo(EditorSnapshot current) {
        if (redo.isEmpty()) {
            return Optional.empty();
        }
        pushUndo(current);
        return Optional.of(redo.removeLast());
    }

    /** What the next undo would restore. */
    public Optional<EditorSnapshot> peekUndo() {
        return undo.isEmpty() ? Optional.empty() : Optional.of(undo.getLast());
    }

    /** The undo snapshots, newest first. */
    public List<EditorSnapshot> history() {
        return List.copyOf(undo.reversed());
    }

    private void pushUndo(EditorSnapshot snapshot) {
        undo.addLast(snapshot);
        if (undo.size() > capacity) {
            undo.removeFirst(); // bounded: forget the oldest state
        }
    }
}
