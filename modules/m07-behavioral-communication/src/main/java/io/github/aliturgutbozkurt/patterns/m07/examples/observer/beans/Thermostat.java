package io.github.aliturgutbozkurt.patterns.m07.examples.observer.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

/**
 * A JavaBean that uses the JDK's built-in Observer, {@link PropertyChangeSupport}: listeners subscribe to all
 * properties or to one named property and receive the old and the new value.
 *
 * @see "m07 lesson, section Observer — the JDK's own observers"
 */
public final class Thermostat {

    /** Operating mode. */
    public enum Mode { OFF, HEAT, COOL }

    private static final int MIN_TARGET = 5;
    private static final int MAX_TARGET = 30;

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private int target = 20;
    private Mode mode = Mode.OFF;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changes.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

    public int getTarget() {
        return target;
    }

    /** Sets the target temperature in °C; fires {@code "target"} only if the value changed. */
    public void setTarget(int newTarget) {
        if (newTarget < MIN_TARGET || newTarget > MAX_TARGET) {
            throw new IllegalArgumentException(
                    "target must be between " + MIN_TARGET + " and " + MAX_TARGET + " °C: " + newTarget);
        }
        int oldTarget = target;
        target = newTarget;
        changes.firePropertyChange("target", oldTarget, newTarget);
    }

    public Mode getMode() {
        return mode;
    }

    /** Sets the mode; fires {@code "mode"} only if the value changed. */
    public void setMode(Mode newMode) {
        Mode oldMode = mode;
        mode = Objects.requireNonNull(newMode, "newMode");
        changes.firePropertyChange("mode", oldMode, newMode);
    }
}
