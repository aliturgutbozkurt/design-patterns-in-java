package io.github.aliturgutbozkurt.patterns.m05.examples.bridge;

import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote.AdvancedRemote;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote.Device;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote.Radio;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote.RemoteControl;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote.Tv;

/**
 * Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/bridge/RemoteDemo.java}
 *
 * @see "m05 lesson, section Bridge"
 */
public final class RemoteDemo {

    private RemoteDemo() {}

    public static void main(String[] args) {
        Device tv = new Tv();
        var basic = new RemoteControl(tv);
        basic.volumeUp();
        show("volume up while off", tv);
        basic.togglePower();
        basic.volumeUp();
        basic.channelUp();
        show("basic remote", tv);

        Device radio = new Radio();
        var advanced = new AdvancedRemote(radio);
        advanced.togglePower();
        advanced.channelUp();
        advanced.saveFavourite("jazz");
        show("advanced remote", radio);
        advanced.mute();
        show("muted", radio);
        advanced.unmute();
        show("unmuted", radio);
        advanced.channelUp();
        advanced.channelUp();
        show("two stations up", radio);
        advanced.goTo("jazz");
        show("back to \"jazz\"", radio);

        new AdvancedRemote(tv).mute();   // the same refined remote drives the other device
        show("advanced on the TV", tv);
    }

    private static void show(String step, Device device) {
        System.out.println(String.format("%-19s", step) + " -> " + device.status());
    }
}
