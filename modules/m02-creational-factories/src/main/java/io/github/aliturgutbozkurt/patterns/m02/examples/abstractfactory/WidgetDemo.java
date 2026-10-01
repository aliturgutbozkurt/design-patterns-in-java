package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory;

import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui.LoginDialog;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui.WidgetFactories;
import java.util.List;

/**
 * Run: {@code java modules/m02-creational-factories/src/main/java/io/github/aliturgutbozkurt/patterns/m02/examples/abstractfactory/WidgetDemo.java}
 * (a real application would pass {@code System.getProperty("os.name")}).
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public final class WidgetDemo {

    private WidgetDemo() {}

    public static void main(String[] args) {
        for (String osName : List.of("Mac OS X", "Windows 11")) {
            System.out.println("-- " + osName);
            System.out.print(new LoginDialog(WidgetFactories.forOs(osName)).render());
        }
    }
}
