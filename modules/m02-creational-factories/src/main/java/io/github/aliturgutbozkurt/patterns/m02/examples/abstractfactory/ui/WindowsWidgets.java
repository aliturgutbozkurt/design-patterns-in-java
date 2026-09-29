package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui;

/**
 * The Windows family. The concrete products are private: clients see only the product interfaces.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public final class WindowsWidgets implements WidgetFactory {

    private static final String PLATFORM = "Windows";

    private record WindowsButton(String label) implements Button {
        @Override public String platform() { return PLATFORM; }
        @Override public String render() { return "[ " + label + " ]"; }
    }

    private record WindowsCheckbox(String label, boolean checked) implements Checkbox {
        @Override public String platform() { return PLATFORM; }
        @Override public String render() { return (checked ? "[x]" : "[ ]") + " " + label; }
    }

    private record WindowsTextField(String label) implements TextField {
        @Override public String platform() { return PLATFORM; }
        @Override public String render() { return label + ": [__________]"; }
    }

    @Override
    public Button button(String label) {
        return new WindowsButton(label);
    }

    @Override
    public Checkbox checkbox(String label, boolean checked) {
        return new WindowsCheckbox(label, checked);
    }

    @Override
    public TextField textField(String label) {
        return new WindowsTextField(label);
    }
}
