package io.github.aliturgutbozkurt.patterns.m05.examples.facade;

import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.ActionLog;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.Amplifier;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.HomeTheaterFacade;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.Lights;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.Projector;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.Screen;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.StreamingPlayer;

/**
 * Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/facade/HomeTheaterDemo.java}
 *
 * @see "m05 lesson, section Facade"
 */
public final class HomeTheaterDemo {

    private HomeTheaterDemo() {}

    public static void main(String[] args) {
        var log = new ActionLog();
        var lights = new Lights(log);
        var theater = new HomeTheaterFacade(new Amplifier(log), new Projector(log), new Screen(log), lights,
                new StreamingPlayer(log));

        step("watchMovie(\"Dune\")", log, () -> theater.watchMovie("Dune"));
        step("lights.dim(30) directly", log, () -> lights.dim(30));
        step("endMovie()", log, theater::endMovie);
        step("endMovie() again", log, theater::endMovie);
    }

    private static void step(String label, ActionLog log, Runnable action) {
        log.clear();
        action.run();
        System.out.println("> " + label);
        if (log.entries().isEmpty()) {
            System.out.println("  (nothing to do)");
        }
        log.entries().forEach(entry -> System.out.println("  " + entry));
    }
}
