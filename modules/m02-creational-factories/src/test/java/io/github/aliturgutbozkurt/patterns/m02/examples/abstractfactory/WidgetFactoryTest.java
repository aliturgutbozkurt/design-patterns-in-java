package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui.LoginDialog;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui.MacWidgets;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui.Widget;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui.WidgetFactories;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui.WidgetFactory;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.ui.WindowsWidgets;
import io.github.aliturgutbozkurt.patterns.m02.support.Console;
import java.util.List;
import org.junit.jupiter.api.Test;

class WidgetFactoryTest {

    @Test
    void everyWidgetFromOneFactoryHasTheSamePlatform() {
        for (WidgetFactory factory : List.of(new MacWidgets(), new WindowsWidgets())) {
            List<Widget> widgets = new LoginDialog(factory).widgets();
            assertThat(widgets).hasSize(4).extracting(Widget::platform).containsOnly(widgets.getFirst().platform());
        }
    }

    @Test
    void macDialogRendersInMacStyle() {
        assertThat(new LoginDialog(new MacWidgets()).render()).isEqualTo("""
                [macOS] Username: (__________)
                [macOS] Password: (__________)
                [macOS] ◉ Remember me
                [macOS] ( Log in )
                """);
    }

    @Test
    void windowsDialogRendersInWindowsStyle() {
        assertThat(new LoginDialog(new WindowsWidgets()).render()).isEqualTo("""
                [Windows] Username: [__________]
                [Windows] Password: [__________]
                [Windows] [x] Remember me
                [Windows] [ Log in ]
                """);
    }

    @Test
    void uncheckedCheckboxesRenderEmpty() {
        assertThat(new MacWidgets().checkbox("News", false).render()).isEqualTo("○ News");
        assertThat(new WindowsWidgets().checkbox("News", false).render()).isEqualTo("[ ] News");
    }

    @Test
    void theFamilyIsChosenOnceFromTheOsName() {
        assertThat(WidgetFactories.forOs("Mac OS X")).isInstanceOf(MacWidgets.class);
        assertThat(WidgetFactories.forOs("Windows 11")).isInstanceOf(WindowsWidgets.class);
        assertThat(WidgetFactories.forOs("Linux")).isInstanceOf(WindowsWidgets.class);
    }

    @Test
    void demoRendersBothFamilies() {
        assertThat(Console.capture(() -> WidgetDemo.main(new String[0]))).isEqualTo("""
                -- Mac OS X
                [macOS] Username: (__________)
                [macOS] Password: (__________)
                [macOS] ◉ Remember me
                [macOS] ( Log in )
                -- Windows 11
                [Windows] Username: [__________]
                [Windows] Password: [__________]
                [Windows] [x] Remember me
                [Windows] [ Log in ]
                """);
    }
}
