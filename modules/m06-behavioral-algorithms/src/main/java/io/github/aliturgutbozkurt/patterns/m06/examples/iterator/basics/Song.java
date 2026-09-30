package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.basics;

import java.util.Objects;

/**
 * A song in a {@link Playlist}; two songs with the same title and artist are equal.
 *
 * @see "m06 lesson, section Iterator"
 */
public record Song(String title, String artist) {

    public Song {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(artist, "artist");
    }
}
