package io.github.aliturgutbozkurt.patterns.m06.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Edit;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Insert;
import java.util.List;

/**
 * Inserts text; undo removes exactly the inserted characters.
 *
 * @see "m06 lesson, section Command"
 */
final class InsertCommand implements TextCommand {

    private final Insert edit;

    InsertCommand(Insert edit) {
        this.edit = edit;
    }

    @Override
    public void execute(StringBuilder text) {
        text.insert(edit.position(), edit.text());
    }

    @Override
    public void undo(StringBuilder text) {
        text.delete(edit.position(), edit.position() + edit.text().length());
    }

    @Override
    public List<Edit> edits() {
        return List.of(edit);
    }
}
