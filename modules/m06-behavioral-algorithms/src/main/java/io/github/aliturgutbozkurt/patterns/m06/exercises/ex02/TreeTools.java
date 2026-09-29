package io.github.aliturgutbozkurt.patterns.m06.exercises.ex02;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/** GIVEN — do not modify. Iterating a {@link BinaryTree} and grouping a stream into runs. */
public interface TreeTools {

    /**
     * The values in order (left subtree, value, right subtree). Lazy and iterative: O(height) memory, no recursion.
     * {@code next()} past the end throws {@code NoSuchElementException}; {@code remove()} is unsupported.
     */
    <T> Iterator<T> inOrder(BinaryTree<T> tree);

    /** The same values as {@link #inOrder}, as a sequential stream whose spliterator is {@code ORDERED | NONNULL}. */
    <T> Stream<T> stream(BinaryTree<T> tree);

    /**
     * Groups <em>consecutive</em> elements while {@code sameRun.test(previous, current)} holds. Each run is emitted as
     * an unmodifiable list; the last run is emitted at the end; an empty stream gives no runs.
     */
    <T> Gatherer<T, ?, List<T>> runs(BiPredicate<? super T, ? super T> sameRun);
}
