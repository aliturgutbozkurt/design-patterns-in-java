package io.github.aliturgutbozkurt.patterns.m06.exercises.ex02;

/**
 * GIVEN — do not modify. An immutable binary tree: either {@link Empty} or a {@link Branch} with two subtrees.
 * Note: the records' {@code equals}, {@code hashCode} and {@code toString} recurse, so never call them on a very deep
 * tree.
 */
public sealed interface BinaryTree<T> permits Empty, Branch {

    static <T> BinaryTree<T> empty() {
        return new Empty<>();
    }

    static <T> BinaryTree<T> leaf(T value) {
        return new Branch<>(empty(), value, empty());
    }

    static <T> BinaryTree<T> branch(BinaryTree<T> left, T value, BinaryTree<T> right) {
        return new Branch<>(left, value, right);
    }
}
