package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.form;

import java.util.Objects;

/**
 * A single-line text input.
 *
 * @see "m07 lesson, section Mediator — GUI forms"
 */
public final class TextField extends Widget {

    private String text = "";

    TextField(String name, SignUpForm form) {
        super(name, form);
    }

    public String text() {
        return text;
    }

    /** The user replaces the text; the form is told about it. */
    public void type(String newText) {
        requireEnabled();
        text = Objects.requireNonNull(newText, "newText");
        changed();
    }

    /** Used by the form only; does not notify it again. */
    void clear() {
        text = "";
    }
}
