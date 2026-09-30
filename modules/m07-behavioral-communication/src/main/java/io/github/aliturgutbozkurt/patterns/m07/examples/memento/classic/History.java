package io.github.aliturgutbozkurt.patterns.m07.examples.memento.classic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

/**
 * Caretaker: keeps mementos in a stack without being able to look inside them.
 *
 * @see "m07 lesson, section Memento"
 */
public final class History {

    private final Deque<TextDocument.Memento> stack = new ArrayDeque<>();

    public void push(TextDocument.Memento memento) {
        stack.push(Objects.requireNonNull(memento, "memento"));
    }

    /** The most recent memento, removed from the stack; empty if there is none. */
    public Optional<TextDocument.Memento> pop() {
        return Optional.ofNullable(stack.poll());
    }

    public int size() {
        return stack.size();
    }
}
