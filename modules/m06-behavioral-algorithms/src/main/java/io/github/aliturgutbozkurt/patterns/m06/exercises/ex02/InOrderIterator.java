package io.github.aliturgutbozkurt.patterns.m06.exercises.ex02;

import java.util.Iterator;

/** Assignment 02 — your iterative in-order iterator (explicit stack, no recursion). */
public class InOrderIterator<T> implements Iterator<T> {

    public InOrderIterator(BinaryTree<T> tree) {
        // TODO(ex02): keep a Deque of branches whose value has not been returned yet; push the left spine of tree.
    }

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException("TODO(ex02): implement hasNext()");
    }

    @Override
    public T next() {
        // TODO(ex02): pop a branch, push the left spine of its right subtree, return its value.
        throw new UnsupportedOperationException("TODO(ex02): implement next()");
    }
}
