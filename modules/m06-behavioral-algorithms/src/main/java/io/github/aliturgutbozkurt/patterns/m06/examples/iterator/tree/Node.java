package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An immutable n-ary tree node. Note: the generated {@code equals}, {@code hashCode} and {@code toString} recurse into
 * the children, so do not call them on very deep trees.
 *
 * @see "m06 lesson, section Iterator"
 */
public record Node<T>(T value, List<Node<T>> children) {

    public Node {
        Objects.requireNonNull(value, "value");
        children = List.copyOf(children);
    }

    @SafeVarargs  // the array is only read element by element, never stored or passed on
    public static <T> Node<T> of(T value, Node<T>... children) {
        List<Node<T>> list = new ArrayList<>(children.length);
        for (Node<T> child : children) {
            list.add(child);
        }
        return new Node<>(value, list);
    }
}
