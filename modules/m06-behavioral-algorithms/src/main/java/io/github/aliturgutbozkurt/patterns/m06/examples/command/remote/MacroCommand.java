package io.github.aliturgutbozkurt.patterns.m06.examples.command.remote;

import java.util.List;

/**
 * A command made of commands: runs them in order and undoes them in reverse order.
 *
 * @see "m06 lesson, section Command"
 */
public final class MacroCommand implements Command {

    private final List<Command> commands;

    public MacroCommand(List<Command> commands) {
        this.commands = List.copyOf(commands);
    }

    @Override
    public void execute() {
        commands.forEach(Command::execute);
    }

    @Override
    public void undo() {
        commands.reversed().forEach(Command::undo);
    }
}
