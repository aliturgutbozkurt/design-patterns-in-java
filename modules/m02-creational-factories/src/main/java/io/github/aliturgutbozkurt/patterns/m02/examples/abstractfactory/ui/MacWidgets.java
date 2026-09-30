package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui;

/**
 * The macOS family. The concrete products are private: clients see only the product interfaces.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public final class MacWidgets implements WidgetFactory {

    private static final String PLATFORM = "macOS";

    private record MacButton(String label) implements Button {
        @Override public String platform() { return PLATFORM; }
        @Override public String render() { return "( " + label + " )"; }
    }

    private record MacCheckbox(String label, boolean checked) implements Checkbox {
        @Override public String platform() { return PLATFORM; }
        @Override public String render() { return (checked ? "◉" : "○") + " " + label; }
    }

    private record MacTextField(String label) implements TextField {
        @Override public String platform() { return PLATFORM; }
        @Override public String render() { return label + ": (__________)"; }
    }

    @Override
    public Button button(String label) {
        return new MacButton(label);
    }

    @Override
    public Checkbox checkbox(String label, boolean checked) {
        return new MacCheckbox(label, checked);
    }

    @Override
    public TextField textField(String label) {
        return new MacTextField(label);
    }
}
