package io.github.aliturgutbozkurt.patterns.m06.examples.command.remote;

/**
 * Receiver: a thermostat with a target temperature in whole degrees Celsius.
 *
 * @see "m06 lesson, section Command"
 */
public final class Thermostat {

    private int temperature;

    public Thermostat(int temperature) {
        this.temperature = temperature;
    }

    public int temperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
