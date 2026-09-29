package io.github.aliturgutbozkurt.patterns.m07.examples.observer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.beans.Thermostat;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.beans.Thermostat.Mode;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ThermostatTest {

    private final Thermostat thermostat = new Thermostat();
    private final List<PropertyChangeEvent> events = new ArrayList<>();

    @Test
    void listenerReceivesPropertyNameOldAndNewValue() {
        thermostat.addPropertyChangeListener(events::add);
        thermostat.setTarget(22);
        assertThat(events).singleElement().satisfies(event -> {
            assertThat(event.getPropertyName()).isEqualTo("target");
            assertThat(event.getOldValue()).isEqualTo(20);
            assertThat(event.getNewValue()).isEqualTo(22);
            assertThat(event.getSource()).isSameAs(thermostat);
        });
    }

    @Test
    void settingAnEqualValueFiresNothing() {
        thermostat.addPropertyChangeListener(events::add);
        thermostat.setTarget(20);
        thermostat.setMode(Mode.OFF);
        assertThat(events).isEmpty();
    }

    @Test
    void listenerForTargetDoesNotSeeModeChanges() {
        thermostat.addPropertyChangeListener("target", events::add);
        thermostat.setMode(Mode.HEAT);
        thermostat.setTarget(21);
        assertThat(events).extracting(PropertyChangeEvent::getPropertyName).containsExactly("target");
    }

    @Test
    void removedListenerReceivesNothing() {
        PropertyChangeListener listener = events::add;
        thermostat.addPropertyChangeListener(listener);
        thermostat.removePropertyChangeListener(listener);
        thermostat.setTarget(23);
        assertThat(events).isEmpty();
    }

    @Test
    void rejectsTargetOutsideTheSupportedRange() {
        assertThatIllegalArgumentException().isThrownBy(() -> thermostat.setTarget(31))
                .withMessage("target must be between 5 and 30 °C: 31");
    }

    @Test
    void demoPrintsPropertyChanges() {
        assertThat(Console.capture(() -> ThermostatDemo.main(new String[0]))).isEqualTo("""
                app:     target 20 -> 22
                display: target 20 -> 22
                app:     mode OFF -> HEAT
                (setting target 22 again fires nothing)
                app:     target 22 -> 19
                display: target 22 -> 19
                """);
    }
}
