package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui;

import java.util.Locale;

/**
 * Chooses the family once — typically in the composition root — from the operating system name.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public final class WidgetFactories {

    private WidgetFactories() {}

    /** {@code MacWidgets} for macOS, {@code WindowsWidgets} otherwise. */
    public static WidgetFactory forOs(String osName) {
        return osName.toLowerCase(Locale.ROOT).startsWith("mac") ? new MacWidgets() : new WindowsWidgets();
    }
}
