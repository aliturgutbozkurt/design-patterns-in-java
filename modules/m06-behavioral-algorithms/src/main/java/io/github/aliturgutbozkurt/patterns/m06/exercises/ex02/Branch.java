package io.github.aliturgutbozkurt.patterns.m06.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. A node with a non-null value and two subtrees (each possibly {@link Empty}). */
public record Branch<T>(BinaryTree<T> left, T value, BinaryTree<T> right) implements BinaryTree<T> {

    public Branch {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(right, "right");
    }
}
