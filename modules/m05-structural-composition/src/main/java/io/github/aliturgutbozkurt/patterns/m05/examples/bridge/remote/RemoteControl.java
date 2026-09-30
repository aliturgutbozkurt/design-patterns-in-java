package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote;

import java.util.Objects;

/**
 * Bridge abstraction: user-level controls written only in terms of {@link Device} primitives, so this one class
 * works with a TV, a radio, or any device added later.
 *
 * @see "m05 lesson, section Bridge"
 */
public class RemoteControl {

    protected static final int VOLUME_STEP = 10;

    protected final Device device;

    public RemoteControl(Device device) {
        this.device = Objects.requireNonNull(device, "device");
    }

    public void togglePower() {
        if (device.isEnabled()) {
            device.disable();
        } else {
            device.enable();
        }
    }

    /** Ten steps up, never above 100; ignored while the device is off. */
    public void volumeUp() {
        if (device.isEnabled()) {
            device.setVolume(Math.min(100, device.volume() + VOLUME_STEP));
        }
    }

    /** Ten steps down, never below 0; ignored while the device is off. */
    public void volumeDown() {
        if (device.isEnabled()) {
            device.setVolume(Math.max(0, device.volume() - VOLUME_STEP));
        }
    }

    /** Next channel, wrapping from the last one back to 1; ignored while the device is off. */
    public void channelUp() {
        if (device.isEnabled()) {
            device.setChannel(device.channel() % device.channelCount() + 1);
        }
    }
}
