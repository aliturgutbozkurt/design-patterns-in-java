package io.github.aliturgutbozkurt.patterns.m06.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Delete;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Edit;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Editor;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Insert;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Replace;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Reference solution for assignment 01: every edit becomes a {@link TextCommand}; the editor is the invoker with a
 * bounded undo stack and a redo stack. A group collects its commands into one {@link GroupCommand}. Not thread-safe.
 *
 * @see "m06 lesson, section Command"
 */
public final class CommandEditor implements Editor {

    private final StringBuilder text;
    private final int maxHistory;
    private final Deque<TextCommand> undoStack = new ArrayDeque<>();
    private final Deque<TextCommand> redoStack = new ArrayDeque<>();
    private List<TextCommand> openGroup;  // null when no group is running

    public CommandEditor(String initialText, int maxHistory) {
        Objects.requireNonNull(initialText, "initialText");
        if (maxHistory < 1) {
            throw new IllegalArgumentException("maxHistory must be >= 1: " + maxHistory);
        }
        this.text = new StringBuilder(initialText);
        this.maxHistory = maxHistory;
    }

    @Override
    public String text() {
        return text.toString();
    }

    @Override
    public void apply(Edit edit) {
        Objects.requireNonNull(edit, "edit");
        TextCommand command = commandFor(edit);  // checks the bounds before anything changes
        command.execute(text);
        if (openGroup != null) {
            openGroup.add(command);
        } else {
            push(command);
        }
    }

    private TextCommand commandFor(Edit edit) {
        return switch (edit) {
            case Insert insert -> {
                Objects.checkIndex(insert.position(), text.length() + 1);
                yield new InsertCommand(insert);
            }
            case Delete delete -> {
                Objects.checkFromIndexSize(delete.position(), delete.length(), text.length());
                yield new DeleteCommand(delete);
            }
            case Replace replace -> {
                Objects.checkFromIndexSize(replace.position(), replace.length(), text.length());
                yield new ReplaceCommand(replace);
            }
        };
    }

    private void push(TextCommand command) {
        undoStack.push(command);
        if (undoStack.size() > maxHistory) {
            undoStack.removeLast();  // drop the oldest step
        }
        redoStack.clear();
    }

    @Override
    public boolean undo() {
        requireNoOpenGroup();
        TextCommand command = undoStack.poll();
        if (command == null) {
            return false;
        }
        command.undo(text);
        redoStack.push(command);
        return true;
    }

    @Override
    public boolean redo() {
        requireNoOpenGroup();
        TextCommand command = redoStack.poll();
        if (command == null) {
            return false;
        }
        command.execute(text);
        undoStack.push(command);
        return true;
    }

    @Override
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    @Override
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    @Override
    public void group(Consumer<Editor> edits) {
        Objects.requireNonNull(edits, "edits");
        boolean outermost = openGroup == null;
        if (outermost) {
            openGroup = new ArrayList<>();
        }
        List<TextCommand> group = openGroup;
        int mark = group.size();  // a nested group rolls back only its own commands
        try {
            edits.accept(this);
        } catch (RuntimeException | Error e) {
            for (int i = group.size() - 1; i >= mark; i--) {
                group.remove(i).undo(text);
            }
            if (outermost) {
                openGroup = null;
            }
            throw e;
        }
        if (outermost) {
            openGroup = null;
            if (!group.isEmpty()) {
                push(new GroupCommand(group));
            }
        }
    }

    @Override
    public List<Edit> undoHistory() {
        return undoStack.stream().flatMap(command -> command.edits().reversed().stream()).toList();
    }

    private void requireNoOpenGroup() {
        if (openGroup != null) {
            throw new IllegalStateException("undo and redo are not allowed inside a group");
        }
    }
}
