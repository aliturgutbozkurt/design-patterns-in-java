package io.github.aliturgutbozkurt.patterns.m06.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m06.exercises.ex02.BinaryTree;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex02.Branch;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex02.Empty;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Reference solution for assignment 02: an in-order iterator with an explicit stack. The stack holds the branches
 * whose value is still to come, at most one per level, so memory is O(height) and nothing recurses.
 *
 * @see "m06 lesson, section Iterator"
 */
public final class InOrderIterator<T> implements Iterator<T> {

    private final Deque<Branch<T>> pending = new ArrayDeque<>();

    public InOrderIterator(BinaryTree<T> tree) {
        pushLeftSpine(Objects.requireNonNull(tree, "tree"));
    }

    /** Walks down the left edge, remembering every branch on the way. */
    private void pushLeftSpine(BinaryTree<T> tree) {
        BinaryTree<T> node = tree;
        boolean more = true;
        while (more) {
            switch (node) {
                case Empty<T> _ -> more = false;
                case Branch<T> branch -> {
                    pending.push(branch);
                    node = branch.left();
                }
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !pending.isEmpty();
    }

    @Override
    public T next() {
        if (pending.isEmpty()) {
            throw new NoSuchElementException("no more values in the tree");
        }
        Branch<T> branch = pending.pop();
        pushLeftSpine(branch.right());
        return branch.value();
    }
}
