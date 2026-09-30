package io.github.aliturgutbozkurt.patterns.m06.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Edit;
import java.util.List;

/**
 * A command on the editor's text buffer (the receiver). It remembers what it needs to undo itself.
 *
 * @see "m06 lesson, section Command"
 */
interface TextCommand {

    void execute(StringBuilder text);

    /** Reverts the last {@link #execute(StringBuilder)}; the text must be as {@code execute} left it. */
    void undo(StringBuilder text);

    /** The edits this command applies, in the order it applies them. */
    List<Edit> edits();
}
