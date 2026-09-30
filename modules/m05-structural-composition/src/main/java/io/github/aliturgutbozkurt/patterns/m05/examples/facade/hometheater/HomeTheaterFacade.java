package io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater;

import java.util.Objects;
import java.util.Optional;

/**
 * Facade: two high-level methods that run the subsystem calls in the right order. The subsystems are injected and
 * stay public — the facade is a convenience, not a wall.
 *
 * @see "m05 lesson, section Facade"
 */
public final class HomeTheaterFacade {

    private final Amplifier amplifier;
    private final Projector projector;
    private final Screen screen;
    private final Lights lights;
    private final StreamingPlayer player;
    private String playing;

    public HomeTheaterFacade(Amplifier amplifier, Projector projector, Screen screen, Lights lights,
                             StreamingPlayer player) {
        this.amplifier = Objects.requireNonNull(amplifier, "amplifier");
        this.projector = Objects.requireNonNull(projector, "projector");
        this.screen = Objects.requireNonNull(screen, "screen");
        this.lights = Objects.requireNonNull(lights, "lights");
        this.player = Objects.requireNonNull(player, "player");
    }

    /** Gets the room ready and starts the film. */
    public void watchMovie(String title) {
        Objects.requireNonNull(title, "title");
        if (playing != null) {
            throw new IllegalStateException("already playing: " + playing);
        }
        lights.dim(10);
        screen.down();
        projector.on();
        projector.wideScreenMode();
        amplifier.on();
        amplifier.setInput("streaming");
        amplifier.setSurroundSound();
        amplifier.setVolume(5);
        player.on();
        player.play(title);
        playing = title;
    }

    /** Shuts everything down in reverse order; does nothing if no film is running. */
    public void endMovie() {
        if (playing == null) {
            return;
        }
        player.stop();
        player.off();
        amplifier.off();
        projector.off();
        screen.up();
        lights.on();
        playing = null;
    }

    public Optional<String> nowPlaying() {
        return Optional.ofNullable(playing);
    }
}
