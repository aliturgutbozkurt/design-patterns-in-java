package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Several ways to walk one tree, each an {@link Iterable}. The callers never see how the tree is stored. Both
 * iterators keep an explicit {@link Deque} instead of recursing, so a very deep tree cannot overflow the call stack.
 *
 * @see "m06 lesson, section Iterator"
 */
public final class TreeTraversals {

    private TreeTraversals() {}

    /** Pre-order: a node, then each child's subtree from left to right. */
    public static <T> Iterable<T> depthFirst(Node<T> root) {
        Objects.requireNonNull(root, "root");
        return () -> new Iterator<>() {
            private final Deque<Node<T>> stack = new ArrayDeque<>();

            {
                stack.push(root);
            }

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public T next() {
                if (stack.isEmpty()) {
                    throw new NoSuchElementException();
                }
                Node<T> node = stack.pop();
                node.children().reversed().forEach(stack::push);  // leftmost child ends up on top
                return node.value();
            }
        };
    }

    /** Level order: the root, then all nodes one level down, and so on. */
    public static <T> Iterable<T> breadthFirst(Node<T> root) {
        Objects.requireNonNull(root, "root");
        return () -> new Iterator<>() {
            private final Deque<Node<T>> queue = new ArrayDeque<>();

            {
                queue.add(root);
            }

            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public T next() {
                if (queue.isEmpty()) {
                    throw new NoSuchElementException();
                }
                Node<T> node = queue.remove();
                queue.addAll(node.children());
                return node.value();
            }
        };
    }
}
