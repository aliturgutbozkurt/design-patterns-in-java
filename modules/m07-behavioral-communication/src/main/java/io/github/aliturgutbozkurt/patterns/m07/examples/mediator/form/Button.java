package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.form;

/**
 * A push button; clicking a disabled button is an error.
 *
 * @see "m07 lesson, section Mediator — GUI forms"
 */
public final class Button extends Widget {

    Button(String name, SignUpForm form) {
        super(name, form);
    }

    public void click() {
        requireEnabled();
        changed();
    }
}
