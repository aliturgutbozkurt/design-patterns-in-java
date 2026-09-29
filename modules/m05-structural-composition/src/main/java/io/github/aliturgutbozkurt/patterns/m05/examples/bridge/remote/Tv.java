package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote;

/**
 * Concrete implementor: a TV with 5 channels. Starts off, on channel 1, at volume 30.
 *
 * @see "m05 lesson, section Bridge"
 */
public final class Tv implements Device {

    private boolean enabled;
    private int volume = 30;
    private int channel = 1;

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
        return channel;
    }

    @Override
    public void setChannel(int channel) {
        if (channel < 1 || channel > channelCount()) {
            throw new IllegalArgumentException("channel must be 1.." + channelCount() + ": " + channel);
        }
        this.channel = channel;
    }

    @Override
    public int channelCount() {
        return 5;
    }

    @Override
    public String status() {
        return "TV: " + (enabled ? "on" : "off") + ", channel " + channel + " of " + channelCount()
                + ", volume " + volume;
    }
}
