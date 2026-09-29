package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The client: builds a login form from whatever family it is given and never names a concrete widget.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public final class LoginDialog {

    private final List<Widget> widgets;

    public LoginDialog(WidgetFactory factory) {
        Objects.requireNonNull(factory, "factory");
        widgets = List.of(
                factory.textField("Username"),
                factory.textField("Password"),
                factory.checkbox("Remember me", true),
                factory.button("Log in"));
    }

    public List<Widget> widgets() {
        return widgets;
    }

    public String render() {
        return widgets.stream()
                .map(widget -> "[" + widget.platform() + "] " + widget.render())
                .collect(Collectors.joining("\n", "", "\n"));
    }
}
