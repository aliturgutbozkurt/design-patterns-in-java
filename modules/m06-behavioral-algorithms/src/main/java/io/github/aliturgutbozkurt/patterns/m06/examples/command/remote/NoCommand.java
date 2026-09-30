package io.github.aliturgutbozkurt.patterns.m06.examples.command.remote;

/**
 * Null Object: the command in every empty slot. It does nothing, so the remote never has to check for {@code null}.
 *
 * @see "m06 lesson, section Command"
 */
public enum NoCommand implements Command {
    INSTANCE;

    @Override
    public void execute() {}

    @Override
    public void undo() {}
}
