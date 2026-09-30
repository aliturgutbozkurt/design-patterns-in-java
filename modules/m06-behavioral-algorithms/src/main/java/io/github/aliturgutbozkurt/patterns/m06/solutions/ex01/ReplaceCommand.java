package io.github.aliturgutbozkurt.patterns.m06.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Edit;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Replace;
import java.util.List;

/**
 * Replaces characters and keeps the replaced text for undo.
 *
 * @see "m06 lesson, section Command"
 */
final class ReplaceCommand implements TextCommand {

    private final Replace edit;
    private String replaced = "";

    ReplaceCommand(Replace edit) {
        this.edit = edit;
    }

    @Override
    public void execute(StringBuilder text) {
        int end = edit.position() + edit.length();
        replaced = text.substring(edit.position(), end);
        text.replace(edit.position(), end, edit.text());
    }

    @Override
    public void undo(StringBuilder text) {
        text.replace(edit.position(), edit.position() + edit.text().length(), replaced);
    }

    @Override
    public List<Edit> edits() {
        return List.of(edit);
    }
}
