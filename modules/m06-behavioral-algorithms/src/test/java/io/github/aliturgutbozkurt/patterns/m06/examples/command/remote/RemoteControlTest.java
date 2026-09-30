package io.github.aliturgutbozkurt.patterns.m06.examples.command.remote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RemoteControlTest {

    private final Light light = new Light();
    private final Thermostat thermostat = new Thermostat(19);
    private final GarageDoor garage = new GarageDoor();
    private final RemoteControl remote = new RemoteControl(4);

    @Test
    void pressingASlotCallsTheRightReceiver() {
        remote.setCommand(0, Command.of(light::on, light::off));
        remote.setCommand(1, Command.of(garage::open, garage::close));
        remote.press(1);
        assertThat(garage.isOpen()).isTrue();
        assertThat(light.isOn()).isFalse();
    }

    @Test
    void anEmptySlotDoesNothing() {
        remote.press(3);
        remote.pressUndo();
        assertThat(light.isOn()).isFalse();
        assertThat(thermostat.temperature()).isEqualTo(19);
        assertThat(garage.isOpen()).isFalse();
    }

    @Test
    void macroRunsInOrderAndUndoesInReverseOrder() {
        var calls = new ArrayList<String>();
        var macro = new MacroCommand(List.of(
                Command.of(() -> calls.add("do a"), () -> calls.add("undo a")),
                Command.of(() -> calls.add("do b"), () -> calls.add("undo b")),
                Command.of(() -> calls.add("do c"), () -> calls.add("undo c"))));
        remote.setCommand(0, macro);
        remote.press(0);
        remote.pressUndo();
        assertThat(calls).containsExactly("do a", "do b", "do c", "undo c", "undo b", "undo a");
    }

    @Test
    void undoUndoesTheLastButtonPressed() {
        remote.setCommand(0, Command.of(light::on, light::off));
        remote.setCommand(1, Command.of(garage::open, garage::close));
        remote.press(0);
        remote.press(1);
        remote.pressUndo();
        assertThat(garage.isOpen()).isFalse();
        assertThat(light.isOn()).isTrue();
    }

    @Test
    void thermostatUndoRestoresThePreviousTemperature() {
        remote.setCommand(2, new SetTemperatureCommand(thermostat, 25));
        thermostat.setTemperature(17);
        remote.press(2);
        assertThat(thermostat.temperature()).isEqualTo(25);
        remote.pressUndo();
        assertThat(thermostat.temperature()).isEqualTo(17);
    }

    @Test
    void unknownSlotIsRejected() {
        assertThatThrownBy(() -> remote.press(4)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void demoPrintsTheHomeAfterEveryButton() {
        assertThat(Console.capture(() -> RemoteControlDemo.main(new String[0]))).isEqualTo("""
                press 0 (light on)     light=on thermostat=19 garage=closed
                press 1 (heat to 22)   light=on thermostat=22 garage=closed
                undo                   light=on thermostat=19 garage=closed
                press 2 (open garage)  light=on thermostat=19 garage=open
                press 4 (empty slot)   light=on thermostat=19 garage=open
                press 3 (movie night)  light=off thermostat=21 garage=closed
                undo                   light=on thermostat=19 garage=open
                """);
    }
}
