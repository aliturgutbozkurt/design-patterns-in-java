package io.github.aliturgutbozkurt.patterns.m07.examples.memento;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m07.examples.memento.game.Game;
import io.github.aliturgutbozkurt.patterns.m07.examples.memento.game.GameState;
import io.github.aliturgutbozkurt.patterns.m07.examples.memento.game.Position;
import io.github.aliturgutbozkurt.patterns.m07.examples.memento.game.SaveSlots;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GameSaveTest {

    private final Game game = new Game();
    private final SaveSlots slots = new SaveSlots(3);

    private static GameState stateAtLevel(int level) {
        return new GameState(level, 100, new Position(0, 0), List.of());
    }

    @Test
    void saveThenPlayThenLoadRestoresTheExactState() {
        game.pickUp("sword");
        game.moveTo(1, 2);
        GameState saved = game.save();
        game.levelUp();
        game.takeDamage(40);
        game.pickUp("shield");
        game.moveTo(9, 9);
        game.load(saved);
        assertThat(game.save()).isEqualTo(new GameState(1, 100, new Position(1, 2), List.of("sword")));
    }

    @Test
    void changingTheInventoryAfterSavingDoesNotChangeTheSnapshot() {
        game.pickUp("sword");
        GameState saved = game.save();
        game.pickUp("key");
        assertThat(saved.inventory()).containsExactly("sword").isUnmodifiable();

        var items = new ArrayList<>(List.of("map"));
        var state = new GameState(1, 50, new Position(0, 0), items);
        items.add("compass");
        assertThat(state.inventory()).containsExactly("map");
    }

    @Test
    void continueLoadsTheMostRecentlyWrittenSlotIncludingAnOverwrittenOlderSlot() {
        slots.save("a", stateAtLevel(1));
        slots.save("b", stateAtLevel(2));
        assertThat(slots.continueLatest()).contains(stateAtLevel(2));
        slots.save("a", stateAtLevel(3));
        assertThat(slots.continueLatest()).contains(stateAtLevel(3));
        assertThat(slots.slotNames()).containsExactly("b", "a");
    }

    @Test
    void savingIntoAFullSetEvictsTheLeastRecentlyWrittenSlot() {
        slots.save("a", stateAtLevel(1));
        slots.save("b", stateAtLevel(2));
        slots.save("c", stateAtLevel(3));
        slots.save("a", stateAtLevel(4)); // overwrite: no eviction, "a" becomes newest
        assertThat(slots.save("d", stateAtLevel(5))).contains("b");
        assertThat(slots.slotNames()).containsExactly("c", "a", "d");
    }

    @Test
    void loadingAnUnknownSlotThrowsWithTheKnownSlotNames() {
        slots.save("a", stateAtLevel(1));
        slots.save("b", stateAtLevel(2));
        assertThatIllegalArgumentException().isThrownBy(() -> slots.load("zzz"))
                .withMessage("no save slot 'zzz'; known slots: [a, b]");
    }

    @Test
    void continueOnNoSavesIsEmpty() {
        assertThat(slots.continueLatest()).isEmpty();
    }

    @Test
    void stateIsValidated() {
        assertThatIllegalArgumentException().isThrownBy(() -> new GameState(0, 100, new Position(0, 0), List.of()));
        assertThatIllegalArgumentException().isThrownBy(() -> new GameState(1, 101, new Position(0, 0), List.of()));
    }

    @Test
    void demoPrintsSavesLoadsContinueAndEviction() {
        assertThat(Console.capture(() -> GameSaveDemo.main(new String[0]))).isEqualTo("""
                save castle:   level 1, health 100, at (3,4), inventory [sword]
                save tower:    level 2, health 70, at (3,4), inventory [sword, key]
                oops:          level 2, health 0, at (3,4), inventory [sword, key]
                load castle:   level 1, health 100, at (3,4), inventory [sword]
                slots:         [tower, castle]
                continue:      level 1, health 100, at (5,5), inventory [sword]
                save forest evicts tower
                load tower:    no save slot 'tower'; known slots: [castle, cave, forest]
                """);
    }
}
