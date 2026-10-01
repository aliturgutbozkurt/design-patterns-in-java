package io.github.aliturgutbozkurt.patterns.m06.examples.command.remote;

import java.util.List;

/**
 * Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/command/remote/RemoteControlDemo.java}
 *
 * @see "m06 lesson, section Command"
 */
public final class RemoteControlDemo {

    private RemoteControlDemo() {}

    public static void main(String[] args) {
        var light = new Light();
        var thermostat = new Thermostat(19);
        var garage = new GarageDoor();

        // The client wires commands to receivers and puts them into slots; the remote only sees Command.
        var remote = new RemoteControl(5);
        remote.setCommand(0, Command.of(light::on, light::off));
        remote.setCommand(1, new SetTemperatureCommand(thermostat, 22));
        remote.setCommand(2, Command.of(garage::open, garage::close));
        remote.setCommand(3, new MacroCommand(List.of(
                Command.of(light::off, light::on),
                new SetTemperatureCommand(thermostat, 21),
                Command.of(garage::close, garage::open))));
        // slot 4 stays empty: it holds NoCommand.INSTANCE

        Runnable status = () -> System.out.println("light=" + (light.isOn() ? "on" : "off")
                + " thermostat=" + thermostat.temperature() + " garage=" + (garage.isOpen() ? "open" : "closed"));

        step("press 0 (light on)", () -> remote.press(0), status);
        step("press 1 (heat to 22)", () -> remote.press(1), status);
        step("undo", remote::pressUndo, status);
        step("press 2 (open garage)", () -> remote.press(2), status);
        step("press 4 (empty slot)", () -> remote.press(4), status);
        step("press 3 (movie night)", () -> remote.press(3), status);
        step("undo", remote::pressUndo, status);
    }

    private static void step(String label, Runnable action, Runnable status) {
        action.run();
        System.out.print(String.format("%-22s ", label));
        status.run();
    }
}
