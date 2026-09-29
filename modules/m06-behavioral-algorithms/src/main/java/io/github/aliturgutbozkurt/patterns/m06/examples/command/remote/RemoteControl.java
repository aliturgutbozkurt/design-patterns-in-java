package io.github.aliturgutbozkurt.patterns.m06.examples.command.remote;

import java.util.Arrays;
import java.util.Objects;

/**
 * The invoker: numbered slots, each holding a {@link Command}, plus an undo button for the last button pressed. It
 * knows nothing about lights, thermostats or doors. Not thread-safe.
 *
 * @see "m06 lesson, section Command"
 */
public final class RemoteControl {

    private final Command[] slots;
    private Command lastPressed = NoCommand.INSTANCE;

    public RemoteControl(int slotCount) {
        if (slotCount <= 0) {
            throw new IllegalArgumentException("slotCount must be > 0: " + slotCount);
        }
        slots = new Command[slotCount];
        Arrays.fill(slots, NoCommand.INSTANCE);
    }

    public void setCommand(int slot, Command command) {
        slots[Objects.checkIndex(slot, slots.length)] = Objects.requireNonNull(command, "command");
    }

    public void press(int slot) {
        Command command = slots[Objects.checkIndex(slot, slots.length)];
        command.execute();
        lastPressed = command;
    }

    /** Undoes the last button pressed; a second press of undo does nothing. */
    public void pressUndo() {
        lastPressed.undo();
        lastPressed = NoCommand.INSTANCE;
    }
}
