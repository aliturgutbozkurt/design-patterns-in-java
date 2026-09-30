package io.github.aliturgutbozkurt.patterns.m06.exercises.ex02;

import static io.github.aliturgutbozkurt.patterns.m06.exercises.ex02.BinaryTree.branch;
import static io.github.aliturgutbozkurt.patterns.m06.exercises.ex02.BinaryTree.empty;
import static io.github.aliturgutbozkurt.patterns.m06.exercises.ex02.BinaryTree.leaf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.stream.Gatherer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** Assignment 02 — in-order tree iterator and a custom Gatherer. Each test is one acceptance criterion of the brief. */
public abstract class Ex02Contract {

    protected abstract TreeTools tools();

    /**
     * <pre>
     *         8
     *      3     10
     *    1   6      14
     *       4 7   13
     * </pre>
     */
    private static BinaryTree<Integer> sampleBst() {
        return branch(
                branch(leaf(1), 3, branch(leaf(4), 6, leaf(7))),
                8,
                branch(empty(), 10, branch(leaf(13), 14, empty())));
    }

    private static <T> List<T> drain(Iterator<T> iterator) {
        var values = new ArrayList<T>();
        iterator.forEachRemaining(values::add);
        return values;
    }

    @Test
    void inOrderVisitsBinarySearchTreeInSortedOrder() {
        assertThat(drain(tools().inOrder(sampleBst()))).containsExactly(1, 3, 4, 6, 7, 8, 10, 13, 14);
    }

    @Test
    void emptyTreeHasNoElements() {
        assertThat(tools().inOrder(empty()).hasNext()).isFalse();
        assertThat(tools().stream(empty()).count()).isZero();
    }

    @Test
    void nextAfterEndThrowsNoSuchElement() {
        Iterator<String> it = tools().inOrder(leaf("only"));
        assertThat(it.next()).isEqualTo("only");
        assertThat(it.hasNext()).isFalse();
        assertThatThrownBy(it::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void removeIsUnsupported() {
        Iterator<Integer> it = tools().inOrder(sampleBst());
        it.next();
        assertThatThrownBy(it::remove).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void independentIteratorsDoNotInterfere() {
        BinaryTree<Integer> tree = sampleBst();
        Iterator<Integer> a = tools().inOrder(tree);
        Iterator<Integer> b = tools().inOrder(tree);
        a.next();
        a.next();
        assertThat(b.next()).isEqualTo(1);
        assertThat(a.next()).isEqualTo(4);
        assertThat(drain(b)).containsExactly(3, 4, 6, 7, 8, 10, 13, 14);
    }

    @Test
    void deepTreeDoesNotOverflowTheStack() {
        BinaryTree<Integer> spine = leaf(0);
        for (int i = 1; i < 100_000; i++) {
            spine = branch(spine, i, empty());  // left spine: in order it yields 0, 1, 2, ...
        }
        Iterator<Integer> it = tools().inOrder(spine);
        int expected = 0;
        while (it.hasNext()) {
            assertThat(it.next()).isEqualTo(expected++);  // compares Integers only, never the tree records
        }
        assertThat(expected).isEqualTo(100_000);
    }

    @Test
    void streamMatchesIterator() {
        assertThat(tools().stream(sampleBst()).toList()).isEqualTo(drain(tools().inOrder(sampleBst())));
    }

    @Test
    void streamReportsOrderedAndNonNull() {
        Stream<Integer> stream = tools().stream(sampleBst());
        assertThat(stream.isParallel()).isFalse();
        Spliterator<Integer> spliterator = stream.spliterator();
        assertThat(spliterator.hasCharacteristics(Spliterator.ORDERED)).isTrue();
        assertThat(spliterator.hasCharacteristics(Spliterator.NONNULL)).isTrue();
    }

    @Test
    void runsGroupsConsecutiveElements() {
        assertThat(Stream.of(1, 2, 3, 7, 8, 10).gather(tools().<Integer>runs((a, b) -> b == a + 1)).toList())
                .containsExactly(List.of(1, 2, 3), List.of(7, 8), List.of(10));
        assertThat(Stream.of("apple", "avocado", "banana", "cherry", "cranberry")
                .gather(tools().<String>runs((a, b) -> a.charAt(0) == b.charAt(0))).toList())
                .containsExactly(List.of("apple", "avocado"), List.of("banana"), List.of("cherry", "cranberry"));
    }

    @Test
    void runsEmitsTheLastRun() {
        assertThat(Stream.of(5).gather(tools().<Integer>runs((a, b) -> true)).toList())
                .containsExactly(List.of(5));
        assertThat(Stream.of(1, 2, 3).gather(tools().<Integer>runs((a, b) -> true)).toList())
                .containsExactly(List.of(1, 2, 3));
    }

    @Test
    void runsOfEmptyStreamIsEmpty() {
        assertThat(Stream.<Integer>empty().gather(tools().<Integer>runs((a, b) -> true)).toList()).isEmpty();
    }

    @Test
    void runsListsAreUnmodifiable() {
        List<List<Integer>> runs = Stream.of(1, 2, 9).gather(tools().<Integer>runs((a, b) -> b == a + 1)).toList();
        assertThatThrownBy(() -> runs.getFirst().add(3)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> runs.getLast().add(10)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @Timeout(10)
    void runsStopsEarlyOnInfiniteStream() {
        List<List<Integer>> firstTwo = Stream.iterate(0, i -> i + 1)
                .gather(tools().<Integer>runs((a, b) -> b % 3 != 0))
                .limit(2)
                .toList();
        assertThat(firstTwo).containsExactly(List.of(0, 1, 2), List.of(3, 4, 5));
        // The integrator must pass on downstream.push(...)'s answer: once downstream rejects, it returns false.
        assertThat(integratorResultAfterRejection(tools().<Integer>runs((a, b) -> false), List.of(1, 2))).isFalse();
    }

    private static <T, A> boolean integratorResultAfterRejection(Gatherer<T, A, List<T>> gatherer, List<T> items) {
        A state = gatherer.initializer().get();
        Gatherer.Downstream<List<T>> rejecting = _ -> false;
        boolean result = true;
        for (T item : items) {
            result = gatherer.integrator().integrate(state, item, rejecting);
        }
        return result;
    }

    @Test
    void treeStreamGatheredIntoRuns() {
        BinaryTree<Integer> keys = branch(branch(leaf(1), 2, leaf(3)), 5, branch(leaf(6), 9, leaf(10)));
        assertThat(tools().stream(keys).gather(tools().<Integer>runs((a, b) -> b == a + 1)).toList())
                .containsExactly(List.of(1, 2, 3), List.of(5, 6), List.of(9, 10));
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> tools().inOrder(null));
        assertThatNullPointerException().isThrownBy(() -> tools().stream(null));
        assertThatNullPointerException().isThrownBy(() -> tools().runs(null));
    }
}
