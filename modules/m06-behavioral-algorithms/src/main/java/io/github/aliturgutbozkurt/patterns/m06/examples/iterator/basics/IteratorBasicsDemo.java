package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.basics;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/** Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/iterator/basics/IteratorBasicsDemo.java} */
public final class IteratorBasicsDemo {

    private IteratorBasicsDemo() {}

    public static void main(String[] args) {
        var range = new IntRange(0, 10, 3);
        var line = new StringBuilder("for-each:  ");
        for (int n : range) {
            line.append(n).append(' ');
        }
        System.out.println(line.toString().strip());

        line = new StringBuilder("desugared: ");
        Iterator<Integer> it = range.iterator();  // what the compiler generates for the for-each loop
        while (it.hasNext()) {
            line.append(it.next()).append(' ');
        }
        System.out.println(line.toString().strip());
        try {
            it.next();
        } catch (NoSuchElementException e) {
            System.out.println("next() after the end -> NoSuchElementException");
        }

        Iterator<Integer> a = range.iterator();
        Iterator<Integer> b = range.iterator();
        System.out.println("two iterators: a=" + a.next() + " b=" + b.next() + " a=" + a.next());

        var playlist = new Playlist();
        for (String title : new String[] {"Intro", "Blue", "Green", "Outro"}) {
            playlist.add(new Song(title, "The Band"));
        }
        System.out.println("playlist:   " + titles(playlist));
        System.out.println("reversed(): " + titles(playlist.reversed()));
        System.out.println("first/last: " + playlist.first().title() + " / " + playlist.last().title());
        System.out.println("add Blue again -> " + playlist.add(new Song("Blue", "The Band")));
        var backwards = playlist.reversed();
        playlist.add(new Song("Bonus", "The Band"));
        System.out.println("the reversed view now starts with " + backwards.getFirst().title());
        try {
            for (Song song : playlist) {
                if (song.title().equals("Blue")) {
                    playlist.add(new Song("Encore", "The Band"));
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("add while iterating -> ConcurrentModificationException");
        }
    }

    private static String titles(Iterable<Song> songs) {
        return StreamSupport.stream(songs.spliterator(), false).map(Song::title).collect(Collectors.joining(", "));
    }
}
