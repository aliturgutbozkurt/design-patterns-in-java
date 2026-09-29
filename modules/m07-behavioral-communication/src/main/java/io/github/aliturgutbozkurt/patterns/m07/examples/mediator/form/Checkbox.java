package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.form;

/**
 * A checkbox.
 *
 * @see "m07 lesson, section Mediator — GUI forms"
 */
public final class Checkbox extends Widget {

    private boolean checked;

    Checkbox(String name, SignUpForm form) {
        super(name, form);
    }

    public boolean isChecked() {
        return checked;
    }

    public void check() {
        setChecked(true);
    }

    public void uncheck() {
        setChecked(false);
    }

    private void setChecked(boolean value) {
        requireEnabled();
        checked = value;
        changed();
    }
}
