package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.classic;

/**
 * Classic Command: a request turned into an object that can run, undo itself and describe itself.
 *
 * @see "m06 lesson, section Command"
 */
public interface Command {

    void execute();

    /** Reverts exactly what the last {@link #execute()} did. */
    void undo();

    /** A short text for menus and history lists, e.g. {@code "set A1=10"}. */
    String label();
}
