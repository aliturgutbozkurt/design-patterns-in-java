package io.github.aliturgutbozkurt.patterns.m06.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m06.exercises.ex02.BinaryTree;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex02.TreeTools;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.stream.Gatherer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Reference solution for assignment 02: the stream is built from the iterator, so only the iterator and the gatherer
 * contain real logic.
 *
 * @see "m06 lesson, section Iterator"
 */
public final class DefaultTreeTools implements TreeTools {

    @Override
    public <T> Iterator<T> inOrder(BinaryTree<T> tree) {
        return new InOrderIterator<>(tree);
    }

    @Override
    public <T> Stream<T> stream(BinaryTree<T> tree) {
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(
                inOrder(tree), Spliterator.ORDERED | Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, false);
    }

    @Override
    public <T> Gatherer<T, ?, List<T>> runs(BiPredicate<? super T, ? super T> sameRun) {
        return RunsGatherer.runs(sameRun);
    }
}
