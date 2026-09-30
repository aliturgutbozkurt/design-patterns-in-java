package io.github.aliturgutbozkurt.patterns.m07.examples.memento;

import io.github.aliturgutbozkurt.patterns.m07.examples.memento.game.Game;
import io.github.aliturgutbozkurt.patterns.m07.examples.memento.game.GameState;
import io.github.aliturgutbozkurt.patterns.m07.examples.memento.game.SaveSlots;

/** Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/memento/GameSaveDemo.java} */
public final class GameSaveDemo {

    private GameSaveDemo() {}

    public static void main(String[] args) {
        var game = new Game();
        var slots = new SaveSlots(3);

        game.pickUp("sword");
        game.moveTo(3, 4);
        slots.save("castle", game.save());
        System.out.println("save castle:   " + game.status());
        game.levelUp();
        game.takeDamage(30);
        game.pickUp("key");
        slots.save("tower", game.save());
        System.out.println("save tower:    " + game.status());
        game.takeDamage(70);
        System.out.println("oops:          " + game.status());

        game.load(slots.load("castle"));
        System.out.println("load castle:   " + game.status());
        game.moveTo(5, 5);
        slots.save("castle", game.save()); // re-saved: castle becomes the most recent slot
        System.out.println("slots:         " + slots.slotNames());
        System.out.println("continue:      " + slots.continueLatest().map(GameState::describe).orElse("-"));

        slots.save("cave", game.save());
        System.out.println("save forest evicts " + slots.save("forest", game.save()).orElse("nothing"));
        try {
            slots.load("tower");
        } catch (IllegalArgumentException e) {
            System.out.println("load tower:    " + e.getMessage());
        }
    }
}
