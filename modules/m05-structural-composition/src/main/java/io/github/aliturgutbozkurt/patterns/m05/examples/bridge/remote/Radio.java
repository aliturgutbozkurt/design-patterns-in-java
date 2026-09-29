package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote;

/**
 * Concrete implementor: a radio with 3 preset stations. Starts off, on station 1, at volume 30.
 *
 * @see "m05 lesson, section Bridge"
 */
public final class Radio implements Device {

    private boolean enabled;
    private int volume = 30;
    private int station = 1;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public int volume() {
        return volume;
    }

    @Override
    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) {
            throw new IllegalArgumentException("volume must be 0..100: " + volume);
        }
        this.volume = volume;
    }

    @Override
    public int channel() {
        return station;
    }

    @Override
    public void setChannel(int station) {
        if (station < 1 || station > channelCount()) {
            throw new IllegalArgumentException("station must be 1.." + channelCount() + ": " + station);
        }
        this.station = station;
    }

    @Override
    public int channelCount() {
        return 3;
    }

    @Override
    public String status() {
        return "Radio: " + (enabled ? "on" : "off") + ", station " + station + " of " + channelCount()
                + ", volume " + volume;
    }
}
