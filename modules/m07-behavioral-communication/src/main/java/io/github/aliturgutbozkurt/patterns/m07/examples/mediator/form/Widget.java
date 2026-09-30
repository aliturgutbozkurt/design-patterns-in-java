package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.form;

import java.util.Objects;

/**
 * Colleague base class: a widget knows its form (the mediator) and reports every change to it; it never knows which
 * other widgets depend on it. Only the form enables or disables widgets.
 *
 * @see "m07 lesson, section Mediator — GUI forms"
 */
public abstract sealed class Widget permits TextField, Checkbox, Button {

    private final String name;
    private final SignUpForm form;
    private boolean enabled = true;

    Widget(String name, SignUpForm form) {
        this.name = Objects.requireNonNull(name, "name");
        this.form = Objects.requireNonNull(form, "form");
    }

    public final String name() {
        return name;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** Called by subclasses after the user changed something: {@code form.changed(this)}. */
    final void changed() {
        form.changed(this);
    }

    final void requireEnabled() {
        if (!enabled) {
            throw new IllegalStateException(name + " is disabled");
        }
    }
}
