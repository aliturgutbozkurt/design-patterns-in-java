package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Refined abstraction: adds mute and favourites on top of the basic remote — still using only {@link Device}
 * primitives, so no device class changes.
 *
 * @see "m05 lesson, section Bridge"
 */
public class AdvancedRemote extends RemoteControl {

    private final Map<String, Integer> favourites = new HashMap<>();
    private int volumeBeforeMute = -1;

    public AdvancedRemote(Device device) {
        super(device);
    }

    /** Silences the device and remembers the volume; ignored while off or already muted. */
    public void mute() {
        if (device.isEnabled() && volumeBeforeMute < 0) {
            volumeBeforeMute = device.volume();
            device.setVolume(0);
        }
    }

    /** Restores the volume from before {@link #mute()}; does nothing if not muted. */
    public void unmute() {
        if (volumeBeforeMute >= 0) {
            device.setVolume(volumeBeforeMute);
            volumeBeforeMute = -1;
        }
    }

    /** Remembers the current channel under a name. */
    public void saveFavourite(String name) {
        favourites.put(Objects.requireNonNull(name, "name"), device.channel());
    }

    /** Jumps to a saved channel. */
    public void goTo(String name) {
        Integer channel = favourites.get(name);
        if (channel == null) {
            throw new IllegalArgumentException("no favourite named " + name);
        }
        device.setChannel(channel);
    }
}
