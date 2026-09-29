package io.github.aliturgutbozkurt.patterns.m07.examples.memento.classic;

import java.util.Objects;

/**
 * Originator of the classic GoF Memento: {@link #save()} captures text and cursor in an opaque {@link Memento} that
 * only this class can read, {@link #restore(Memento)} puts them back.
 *
 * @see "m07 lesson, section Memento"
 */
public final class TextDocument {

    /**
     * The snapshot: private fields, no accessors, private constructor. Only the enclosing {@code TextDocument} can
     * create or read it, so a caretaker can store it but never look inside.
     *
     * @see "m07 lesson, section Memento"
     */
    public static final class Memento {
        private final String text;
        private final int cursor;

        private Memento(String text, int cursor) {
            this.text = text;
            this.cursor = cursor;
        }
    }

    private final StringBuilder text = new StringBuilder();
    private int cursor;

    /** Inserts {@code input} at the cursor and moves the cursor behind it. */
    public void type(String input) {
        text.insert(cursor, Objects.requireNonNull(input, "input"));
        cursor += input.length();
    }

    public void moveCursor(int position) {
        if (position < 0 || position > text.length()) {
            throw new IndexOutOfBoundsException("cursor " + position + " outside 0.." + text.length());
        }
        cursor = position;
    }

    public String text() {
        return text.toString();
    }

    public int cursor() {
        return cursor;
    }

    /** The text with a {@code |} at the cursor, e.g. {@code "Hello|, world"}. */
    public String render() {
        return text.substring(0, cursor) + "|" + text.substring(cursor);
    }

    public Memento save() {
        return new Memento(text.toString(), cursor);
    }

    public void restore(Memento memento) {
        Objects.requireNonNull(memento, "memento");
        text.setLength(0);
        text.append(memento.text);
        cursor = memento.cursor;
    }
}
