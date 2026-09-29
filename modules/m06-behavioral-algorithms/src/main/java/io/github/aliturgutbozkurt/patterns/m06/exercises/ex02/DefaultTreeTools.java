package io.github.aliturgutbozkurt.patterns.m06.exercises.ex02;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/** Assignment 02 — wires your iterator and gatherer to the {@link TreeTools} interface. */
public class DefaultTreeTools implements TreeTools {

    @Override
    public <T> Iterator<T> inOrder(BinaryTree<T> tree) {
        throw new UnsupportedOperationException("TODO(ex02): implement inOrder(BinaryTree) with InOrderIterator");
    }

    @Override
    public <T> Stream<T> stream(BinaryTree<T> tree) {
        // TODO(ex02): build the stream from inOrder(tree) — see Hint 2 in the brief.
        throw new UnsupportedOperationException("TODO(ex02): implement stream(BinaryTree)");
    }

    @Override
    public <T> Gatherer<T, ?, List<T>> runs(BiPredicate<? super T, ? super T> sameRun) {
        throw new UnsupportedOperationException("TODO(ex02): implement runs(BiPredicate) with RunsGatherer");
    }
}
