package io.github.aliturgutbozkurt.patterns.m06.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Edit;
import java.util.List;

/**
 * Macro command: the commands of one group, executed in order and undone in reverse order as one step.
 *
 * @see "m06 lesson, section Command"
 */
final class GroupCommand implements TextCommand {

    private final List<TextCommand> commands;

    GroupCommand(List<TextCommand> commands) {
        this.commands = List.copyOf(commands);
    }

    @Override
    public void execute(StringBuilder text) {
        commands.forEach(command -> command.execute(text));
    }

    @Override
    public void undo(StringBuilder text) {
        commands.reversed().forEach(command -> command.undo(text));
    }

    @Override
    public List<Edit> edits() {
        return commands.stream().flatMap(command -> command.edits().stream()).toList();
    }
}
