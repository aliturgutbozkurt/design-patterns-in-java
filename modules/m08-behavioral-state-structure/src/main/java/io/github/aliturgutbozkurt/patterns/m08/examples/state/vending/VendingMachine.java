package io.github.aliturgutbozkurt.patterns.m08.examples.state.vending;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * State context: a snack machine that delegates every operation to its current {@link VendingState}. The context
 * validates arguments and owns the stock, but never asks which state it is in.
 *
 * @see "m08 lesson, section State — Classic Java"
 */
public final class VendingMachine {

    private final SortedMap<String, Product> products;
    private final Map<String, Integer> stock = new TreeMap<>();
    private final List<Dispensed> tray = new ArrayList<>();
    private VendingState state = new SoldOutState();

    /** A machine with the given slots, all empty: it starts {@code SoldOut} until the first restock. */
    public VendingMachine(Map<String, Product> products) {
        this.products = new TreeMap<>(products);
        this.products.keySet().forEach(slot -> stock.put(slot, 0));
    }

    public String insertCoin(int cents) {
        if (cents <= 0) {
            throw new IllegalArgumentException("coin must be positive: " + cents);
        }
        return state.insertCoin(this, cents);
    }

    public String select(String slot) {
        requireSlot(slot);
        return state.select(this, slot);
    }

    /** Hands back the whole credit, in cents (0 if there is none). */
    public int refund() {
        return state.refund(this);
    }

    public String restock(String slot, int count) {
        requireSlot(slot);
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive: " + count);
        }
        return state.restock(this, slot, count);
    }

    public String stateName() {
        return state.name();
    }

    /** Everything dispensed so far, oldest first (a read-only copy). */
    public List<Dispensed> tray() {
        return List.copyOf(tray);
    }

    // --- used by the states (package-private) ---

    void changeState(VendingState next) {
        state = Objects.requireNonNull(next, "next");
    }

    Product product(String slot) {
        return products.get(slot);
    }

    int stock(String slot) {
        return stock.get(slot);
    }

    boolean hasStock() {
        return stock.values().stream().anyMatch(count -> count > 0);
    }

    String addStock(String slot, int count) {
        stock.merge(slot, count, Integer::sum);
        return "restocked " + count + " x " + products.get(slot).name() + " in " + slot;
    }

    void dispense(String slot, Dispensed item) {
        stock.merge(slot, -1, Integer::sum);
        tray.add(item);
    }

    private void requireSlot(String slot) {
        Objects.requireNonNull(slot, "slot");
        if (!products.containsKey(slot)) {
            throw new IllegalArgumentException("unknown slot: " + slot);
        }
    }
}
