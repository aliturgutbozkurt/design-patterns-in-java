package io.github.aliturgutbozkurt.patterns.m08.examples.state;

import io.github.aliturgutbozkurt.patterns.m08.examples.state.vending.Product;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.vending.VendingMachine;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Run: {@code java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/state/VendingMachineDemo.java}
 *
 * @see "m08 lesson, section State"
 */
public final class VendingMachineDemo {

    private VendingMachineDemo() {}

    public static void main(String[] args) {
        var machine = new VendingMachine(Map.of("A1", new Product("Cola", 150), "A2", new Product("Chips", 120)));

        row("before", "action", "after", "message");
        step(machine, "insert 100", () -> machine.insertCoin(100));
        step(machine, "restock A1 2", () -> machine.restock("A1", 2));
        step(machine, "restock A2 1", () -> machine.restock("A2", 1));
        step(machine, "select A1", () -> machine.select("A1"));
        step(machine, "insert 100", () -> machine.insertCoin(100));
        step(machine, "select A1", () -> machine.select("A1"));
        step(machine, "insert 100", () -> machine.insertCoin(100));
        step(machine, "select A1", () -> machine.select("A1"));
        step(machine, "insert 200", () -> machine.insertCoin(200));
        step(machine, "refund", () -> "refunded " + machine.refund());
        step(machine, "insert 120", () -> machine.insertCoin(120));
        step(machine, "select A2", () -> machine.select("A2"));
        step(machine, "insert 150", () -> machine.insertCoin(150));
        step(machine, "select A1", () -> machine.select("A1"));
        System.out.println("tray: " + machine.tray());
    }

    private static void step(VendingMachine machine, String action, Supplier<String> operation) {
        String before = machine.stateName();
        String message = operation.get();
        row(before, action, machine.stateName(), message);
    }

    private static void row(String before, String action, String after, String message) {
        System.out.print(String.format(Locale.ROOT, "%-10s %-14s %-10s %s\n", before, action, after, message));
    }
}
