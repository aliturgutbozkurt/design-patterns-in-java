package io.github.aliturgutbozkurt.patterns.m06.examples.command.remote;

import java.util.Objects;

/**
 * The only type the {@link RemoteControl} knows. Simple commands are two method references joined by
 * {@link #of(Runnable, Runnable)}; commands that must remember state to undo are small classes.
 *
 * @see "m06 lesson, section Command"
 */
public interface Command {

    void execute();

    void undo();

    /** A command whose undo is a fixed action, e.g. {@code Command.of(light::on, light::off)}. */
    static Command of(Runnable execute, Runnable undo) {
        Objects.requireNonNull(execute, "execute");
        Objects.requireNonNull(undo, "undo");
        return new Command() {
            @Override
            public void execute() {
                execute.run();
            }

            @Override
            public void undo() {
                undo.run();
            }
        };
    }
}
