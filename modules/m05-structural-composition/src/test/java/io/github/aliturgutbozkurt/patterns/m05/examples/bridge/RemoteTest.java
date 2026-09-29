package io.github.aliturgutbozkurt.patterns.m05.examples.bridge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote.AdvancedRemote;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote.Device;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote.Radio;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote.RemoteControl;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote.Tv;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class RemoteTest {

    @Test
    void everyRemoteWorksWithEveryDevice() {
        List<Supplier<Device>> devices = List.of(Tv::new, Radio::new);
        List<Function<Device, RemoteControl>> remotes = List.of(RemoteControl::new, AdvancedRemote::new);
        for (Supplier<Device> newDevice : devices) {
            for (Function<Device, RemoteControl> newRemote : remotes) {
                Device device = newDevice.get();
                RemoteControl remote = newRemote.apply(device);
                remote.togglePower();
                remote.volumeUp();
                remote.channelUp();
                assertThat(device.isEnabled()).isTrue();
                assertThat(device.volume()).isEqualTo(40);
                assertThat(device.channel()).isEqualTo(2);
                remote.togglePower();
                assertThat(device.isEnabled()).isFalse();
            }
        }
    }

    @Test
    void volumeClampsBetweenZeroAndHundred() {
        var tv = new Tv();
        var remote = new RemoteControl(tv);
        remote.togglePower();
        for (int i = 0; i < 20; i++) {
            remote.volumeUp();
        }
        assertThat(tv.volume()).isEqualTo(100);
        for (int i = 0; i < 20; i++) {
            remote.volumeDown();
        }
        assertThat(tv.volume()).isZero();
    }

    @Test
    void devicesRejectOutOfRangeValues() {
        var radio = new Radio();
        assertThatIllegalArgumentException().isThrownBy(() -> radio.setVolume(101));
        assertThatIllegalArgumentException().isThrownBy(() -> radio.setChannel(4));
        assertThatIllegalArgumentException().isThrownBy(() -> new Tv().setChannel(0));
    }

    @Test
    void muteAndUnmuteRestoreThePreviousVolume() {
        var tv = new Tv();
        var remote = new AdvancedRemote(tv);
        remote.togglePower();
        remote.volumeUp();
        remote.mute();
        assertThat(tv.volume()).isZero();
        remote.unmute();
        assertThat(tv.volume()).isEqualTo(40);
        remote.unmute();
        assertThat(tv.volume()).isEqualTo(40);
    }

    @Test
    void channelWrapsAfterTheDevicesLastChannel() {
        var radio = new Radio();
        var remote = new RemoteControl(radio);
        remote.togglePower();
        remote.channelUp();
        remote.channelUp();
        assertThat(radio.channel()).isEqualTo(3);
        remote.channelUp();
        assertThat(radio.channel()).isEqualTo(1);

        var tv = new Tv();
        var tvRemote = new RemoteControl(tv);
        tvRemote.togglePower();
        for (int i = 0; i < 5; i++) {
            tvRemote.channelUp();
        }
        assertThat(tv.channel()).isEqualTo(1);
    }

    @Test
    void poweredOffDeviceIgnoresVolumeChanges() {
        var tv = new Tv();
        var remote = new AdvancedRemote(tv);
        remote.volumeUp();
        remote.volumeDown();
        remote.mute();
        assertThat(tv.volume()).isEqualTo(30);
        assertThat(tv.isEnabled()).isFalse();
    }

    @Test
    void favouritesJumpToASavedChannel() {
        var tv = new Tv();
        var remote = new AdvancedRemote(tv);
        remote.togglePower();
        remote.channelUp();
        remote.channelUp();
        remote.saveFavourite("news");
        remote.channelUp();
        remote.goTo("news");
        assertThat(tv.channel()).isEqualTo(3);
        assertThatIllegalArgumentException().isThrownBy(() -> remote.goTo("sport"))
                .withMessage("no favourite named sport");
    }

    @Test
    void demoPrintsDeviceStatesAfterEachStep() {
        assertThat(Console.capture(() -> RemoteDemo.main(new String[0]))).isEqualTo("""
                volume up while off -> TV: off, channel 1 of 5, volume 30
                basic remote        -> TV: on, channel 2 of 5, volume 40
                advanced remote     -> Radio: on, station 2 of 3, volume 30
                muted               -> Radio: on, station 2 of 3, volume 0
                unmuted             -> Radio: on, station 2 of 3, volume 30
                two stations up     -> Radio: on, station 1 of 3, volume 30
                back to "jazz"      -> Radio: on, station 2 of 3, volume 30
                advanced on the TV  -> TV: on, channel 2 of 5, volume 0
                """);
    }
}
