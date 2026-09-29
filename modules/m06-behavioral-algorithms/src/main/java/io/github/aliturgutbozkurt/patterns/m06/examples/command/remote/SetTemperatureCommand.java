package io.github.aliturgutbozkurt.patterns.m06.examples.command.remote;

import java.util.Objects;

/**
 * An undoable command that needs state: it remembers the temperature before it ran, so a lambda is not enough.
 *
 * @see "m06 lesson, section Command"
 */
public final class SetTemperatureCommand implements Command {

    private final Thermostat thermostat;
    private final int target;
    private int previous;

    public SetTemperatureCommand(Thermostat thermostat, int target) {
        this.thermostat = Objects.requireNonNull(thermostat, "thermostat");
        this.target = target;
    }

    @Override
    public void execute() {
        previous = thermostat.temperature();
        thermostat.setTemperature(target);
    }

    @Override
    public void undo() {
        thermostat.setTemperature(previous);
    }
}
