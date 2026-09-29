package io.github.aliturgutbozkurt.patterns.m07.examples.memento.editor;

import java.util.Objects;

/**
 * Modern memento: an immutable record of the whole editor state. It is validated once, in the compact constructor,
 * and can be shared freely because nobody can change it.
 *
 * @see "m07 lesson, section Memento — Modern Java 27"
 */
public record EditorSnapshot(String text, int cursor, int selectionStart, int selectionEnd) {

    public EditorSnapshot {
        Objects.requireNonNull(text, "text");
        if (cursor < 0 || cursor > text.length()) {
            throw new IllegalArgumentException("cursor " + cursor + " outside 0.." + text.length());
        }
        if (selectionStart < 0 || selectionStart > selectionEnd || selectionEnd > text.length()) {
            throw new IllegalArgumentException(
                    "selection " + selectionStart + ".." + selectionEnd + " outside 0.." + text.length());
        }
    }

    /** {@code "Hello [world]"} when something is selected, otherwise the text with {@code |} at the cursor. */
    public String render() {
        if (selectionStart < selectionEnd) {
            return text.substring(0, selectionStart) + "[" + text.substring(selectionStart, selectionEnd) + "]"
                    + text.substring(selectionEnd);
        }
        return text.substring(0, cursor) + "|" + text.substring(cursor);
    }
}
