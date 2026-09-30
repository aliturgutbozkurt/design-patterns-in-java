package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui;

/**
 * Abstract Factory: one creation method per product of the family. A client that only talks to this interface can
 * never mix a Mac button with a Windows checkbox.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public interface WidgetFactory {

    Button button(String label);

    Checkbox checkbox(String label, boolean checked);

    TextField textField(String label);
}
