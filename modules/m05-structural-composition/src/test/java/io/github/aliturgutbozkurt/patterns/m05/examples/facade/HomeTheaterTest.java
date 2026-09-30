package io.github.aliturgutbozkurt.patterns.m05.examples.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.ActionLog;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.Amplifier;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.HomeTheaterFacade;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.Lights;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.Projector;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.Screen;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater.StreamingPlayer;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import org.junit.jupiter.api.Test;

class HomeTheaterTest {

    private final ActionLog log = new ActionLog();
    private final Lights lights = new Lights(log);
    private final HomeTheaterFacade theater = new HomeTheaterFacade(new Amplifier(log), new Projector(log),
            new Screen(log), lights, new StreamingPlayer(log));

    @Test
    void watchMovieRunsTenSubsystemCallsInOrder() {
        theater.watchMovie("Dune");
        assertThat(log.entries()).containsExactly(
                "lights: dim to 10%",
                "screen: down",
                "projector: on",
                "projector: widescreen mode",
                "amplifier: on",
                "amplifier: input streaming",
                "amplifier: surround sound",
                "amplifier: volume 5",
                "player: on",
                "player: play \"Dune\"");
        assertThat(theater.nowPlaying()).contains("Dune");
    }

    @Test
    void endMovieShutsDownInReverseOrder() {
        theater.watchMovie("Dune");
        log.clear();
        theater.endMovie();
        assertThat(log.entries()).containsExactly(
                "player: stop",
                "player: off",
                "amplifier: off",
                "projector: off",
                "screen: up",
                "lights: on");
        assertThat(theater.nowPlaying()).isEmpty();
    }

    @Test
    void endMovieWithoutARunningMovieDoesNothing() {
        theater.endMovie();
        assertThat(log.entries()).isEmpty();
    }

    @Test
    void watchingASecondMovieRequiresEndingTheFirst() {
        theater.watchMovie("Dune");
        assertThatIllegalStateException().isThrownBy(() -> theater.watchMovie("Alien"))
                .withMessage("already playing: Dune");
    }

    @Test
    void subsystemsRemainUsableDirectly() {
        theater.watchMovie("Dune");
        lights.dim(30);   // the facade simplifies; it does not lock the subsystem away
        assertThat(log.entries()).last().isEqualTo("lights: dim to 30%");
    }

    @Test
    void demoPrintsTheLoggedActions() {
        assertThat(Console.capture(() -> HomeTheaterDemo.main(new String[0]))).isEqualTo("""
                > watchMovie("Dune")
                  lights: dim to 10%
                  screen: down
                  projector: on
                  projector: widescreen mode
                  amplifier: on
                  amplifier: input streaming
                  amplifier: surround sound
                  amplifier: volume 5
                  player: on
                  player: play "Dune"
                > lights.dim(30) directly
                  lights: dim to 30%
                > endMovie()
                  player: stop
                  player: off
                  amplifier: off
                  projector: off
                  screen: up
                  lights: on
                > endMovie() again
                  (nothing to do)
                """);
    }
}
