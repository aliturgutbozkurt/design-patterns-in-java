package io.github.aliturgutbozkurt.patterns.m06.examples.command.remote;

/**
 * Receiver: a light that can be switched on and off.
 *
 * @see "m06 lesson, section Command"
 */
public final class Light {

    private boolean on;

    public void on() {
        on = true;
    }

    public void off() {
        on = false;
    }

    public boolean isOn() {
        return on;
    }
}
