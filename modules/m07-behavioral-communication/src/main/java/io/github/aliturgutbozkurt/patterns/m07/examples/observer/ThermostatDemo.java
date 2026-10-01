package io.github.aliturgutbozkurt.patterns.m07.examples.observer;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.beans.Thermostat;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.beans.Thermostat.Mode;
import java.beans.PropertyChangeEvent;

/**
 * Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/observer/ThermostatDemo.java}
 *
 * @see "m07 lesson, section Observer"
 */
public final class ThermostatDemo {

    private ThermostatDemo() {}

    public static void main(String[] args) {
        var thermostat = new Thermostat();
        thermostat.addPropertyChangeListener(event -> print("app:     ", event));            // every property
        thermostat.addPropertyChangeListener("target", event -> print("display: ", event)); // one property

        thermostat.setTarget(22);
        thermostat.setMode(Mode.HEAT);
        System.out.println("(setting target 22 again fires nothing)");
        thermostat.setTarget(22);
        thermostat.setTarget(19);
    }

    private static void print(String who, PropertyChangeEvent event) {
        System.out.println(who + event.getPropertyName() + " " + event.getOldValue() + " -> " + event.getNewValue());
    }
}
