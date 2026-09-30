package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.classic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * The invoker: runs commands and keeps two stacks, one to undo and one to redo. It never looks inside a command.
 * Not thread-safe.
 *
 * @see "m06 lesson, section Command"
 */
public final class UndoManager {

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        command.execute();
        undoStack.push(command);
        redoStack.clear();  // a new action starts a new branch of history: the old "future" is gone
    }

    /** Undoes the most recent command; {@code false} if there is nothing to undo. */
    public boolean undo() {
        Command command = undoStack.poll();
        if (command == null) {
            return false;
        }
        command.undo();
        redoStack.push(command);
        return true;
    }

    /** Re-runs the most recently undone command; {@code false} if there is nothing to redo. */
    public boolean redo() {
        Command command = redoStack.poll();
        if (command == null) {
            return false;
        }
        command.execute();
        undoStack.push(command);
        return true;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /** Labels of the commands that can be undone, newest first. */
    public List<String> undoLabels() {
        return undoStack.stream().map(Command::label).toList();
    }

    /** Labels of the commands that can be redone, next one first. */
    public List<String> redoLabels() {
        return redoStack.stream().map(Command::label).toList();
    }
}
