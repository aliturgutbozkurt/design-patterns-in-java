package io.github.aliturgutbozkurt.patterns.m06.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Delete;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Edit;
import java.util.List;

/**
 * Deletes characters and keeps the removed text, so undo can put back exactly what was there.
 *
 * @see "m06 lesson, section Command"
 */
final class DeleteCommand implements TextCommand {

    private final Delete edit;
    private String removed = "";

    DeleteCommand(Delete edit) {
        this.edit = edit;
    }

    @Override
    public void execute(StringBuilder text) {
        int end = edit.position() + edit.length();
        removed = text.substring(edit.position(), end);
        text.delete(edit.position(), end);
    }

    @Override
    public void undo(StringBuilder text) {
        text.insert(edit.position(), removed);
    }

    @Override
    public List<Edit> edits() {
        return List.of(edit);
    }
}
