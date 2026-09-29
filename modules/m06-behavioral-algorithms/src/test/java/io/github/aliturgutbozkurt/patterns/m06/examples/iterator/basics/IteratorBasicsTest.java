package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.basics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import org.junit.jupiter.api.Test;

class IteratorBasicsTest {

    @Test
    void intRangeYieldsStartToEndExclusiveWithAStep() {
        var seen = new ArrayList<Integer>();
        for (int n : new IntRange(2, 11, 3)) {
            seen.add(n);
        }
        assertThat(seen).containsExactly(2, 5, 8);
        assertThat(new IntRange(0, 5, 1)).containsExactly(0, 1, 2, 3, 4);
        assertThat(new IntRange(5, 5, 1)).isEmpty();
        assertThat(new IntRange(2, 11, 3).size()).isEqualTo(3);
    }

    @Test
    void nextPastTheEndThrowsNoSuchElement() {
        Iterator<Integer> it = new IntRange(0, 1, 1).iterator();
        it.next();
        assertThat(it.hasNext()).isFalse();
        assertThatThrownBy(it::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void iteratorsAreIndependent() {
        var range = new IntRange(0, 3, 1);
        Iterator<Integer> a = range.iterator();
        Iterator<Integer> b = range.iterator();
        a.next();
        a.next();
        assertThat(b.next()).isZero();
        assertThat(a.next()).isEqualTo(2);
    }

    @Test
    void rangeNearMaxValueDoesNotOverflow() {
        assertThat(new IntRange(Integer.MAX_VALUE - 2, Integer.MAX_VALUE, 5)).containsExactly(Integer.MAX_VALUE - 2);
    }

    @Test
    void rangeStreamKnowsItsSize() {
        Spliterator<Integer> spliterator = new IntRange(0, 10, 2).spliterator();
        assertThat(spliterator.hasCharacteristics(Spliterator.SIZED | Spliterator.ORDERED)).isTrue();
        assertThat(spliterator.getExactSizeIfKnown()).isEqualTo(5);
        assertThat(new IntRange(0, 10, 2).stream().mapToInt(Integer::intValue).sum()).isEqualTo(20);
    }

    @Test
    void stepMustBePositive() {
        assertThatIllegalArgumentException().isThrownBy(() -> new IntRange(0, 10, 0));
    }

    @Test
    void playlistKeepsInsertionOrderWithoutDuplicates() {
        var playlist = playlist("Intro", "Blue", "Green");
        assertThat(playlist.add(new Song("Blue", "X"))).isFalse();
        assertThat(playlist).extracting(Song::title).containsExactly("Intro", "Blue", "Green");
        assertThat(playlist.first().title()).isEqualTo("Intro");
        assertThat(playlist.last().title()).isEqualTo("Green");
    }

    @Test
    void reversedIsALiveView() {
        var playlist = playlist("Intro", "Blue");
        var backwards = playlist.reversed();
        playlist.add(new Song("Bonus", "X"));
        assertThat(backwards).extracting(Song::title).containsExactly("Bonus", "Blue", "Intro");
        assertThatThrownBy(() -> backwards.add(new Song("Hack", "X"))).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void addingASongWhileIteratingThrowsConcurrentModification() {
        var playlist = playlist("Intro", "Blue", "Green");
        assertThatThrownBy(() -> {
            for (Song song : playlist) {
                if (song.title().equals("Intro")) {
                    playlist.add(new Song("Encore", "X"));
                }
            }
        }).isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    void demoPrintsLoopsAndPlaylistViews() {
        assertThat(Console.capture(() -> IteratorBasicsDemo.main(new String[0]))).isEqualTo("""
                for-each:  0 3 6 9
                desugared: 0 3 6 9
                next() after the end -> NoSuchElementException
                two iterators: a=0 b=0 a=3
                playlist:   Intro, Blue, Green, Outro
                reversed(): Outro, Green, Blue, Intro
                first/last: Intro / Outro
                add Blue again -> false
                the reversed view now starts with Bonus
                add while iterating -> ConcurrentModificationException
                """);
    }

    private static Playlist playlist(String... titles) {
        var playlist = new Playlist();
        for (String title : titles) {
            playlist.add(new Song(title, "X"));
        }
        return playlist;
    }
}
