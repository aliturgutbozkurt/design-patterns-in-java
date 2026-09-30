package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.basics;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.SequencedCollection;
import java.util.SequencedSet;

/**
 * A playlist backed by a {@link SequencedSet}: insertion order, no duplicates, and {@code reversed()},
 * {@code getFirst()}, {@code getLast()} for free. No reverse iterator has to be written by hand. Not thread-safe.
 *
 * @see "m06 lesson, section Iterator"
 */
public final class Playlist implements Iterable<Song> {

    private final SequencedSet<Song> songs = new LinkedHashSet<>();

    /** Adds the song at the end; {@code false} if it is already in the playlist. */
    public boolean add(Song song) {
        return songs.add(Objects.requireNonNull(song, "song"));
    }

    public Song first() {
        return songs.getFirst();
    }

    public Song last() {
        return songs.getLast();
    }

    /** A live, read-only view in reverse order: songs added later show up in it. */
    public SequencedCollection<Song> reversed() {
        return Collections.unmodifiableSequencedSet(songs).reversed();
    }

    /** Fail-fast: changing the playlist during the loop makes the next {@code next()} throw. */
    @Override
    public Iterator<Song> iterator() {
        return Collections.unmodifiableSequencedSet(songs).iterator();
    }
}
