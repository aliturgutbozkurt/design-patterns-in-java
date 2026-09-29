package io.github.aliturgutbozkurt.patterns.m07.examples.memento.game;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;

/**
 * Caretaker: named save slots in write order, as a {@link SequencedMap}. {@code putLast} makes a re-saved slot the
 * most recent one, {@code lastEntry()} is "Continue", and {@code pollFirstEntry()} evicts the oldest slot when full.
 *
 * @see "m07 lesson, section Memento — Modern Java 27"
 */
public final class SaveSlots {

    private final int maxSlots;
    private final SequencedMap<String, GameState> slots = new LinkedHashMap<>();

    public SaveSlots(int maxSlots) {
        if (maxSlots < 1) {
            throw new IllegalArgumentException("maxSlots must be positive: " + maxSlots);
        }
        this.maxSlots = maxSlots;
    }

    /** Writes {@code state} into {@code slot}; returns the name of the slot evicted to make room, if any. */
    public Optional<String> save(String slot, GameState state) {
        Objects.requireNonNull(slot, "slot");
        Objects.requireNonNull(state, "state");
        Optional<String> evicted = Optional.empty();
        if (!slots.containsKey(slot) && slots.size() == maxSlots) {
            evicted = Optional.of(slots.pollFirstEntry().getKey()); // least recently written
        }
        slots.putLast(slot, state); // put() would keep an existing slot at its old position
        return evicted;
    }

    public GameState load(String slot) {
        GameState state = slots.get(slot);
        if (state == null) {
            throw new IllegalArgumentException("no save slot '" + slot + "'; known slots: " + slotNames());
        }
        return state;
    }

    /** The most recently written state ("Continue"). */
    public Optional<GameState> continueLatest() {
        return Optional.ofNullable(slots.lastEntry()).map(Map.Entry::getValue);
    }

    /** Slot names, least recently written first. */
    public List<String> slotNames() {
        return List.copyOf(slots.sequencedKeySet());
    }
}
