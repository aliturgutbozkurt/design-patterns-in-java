package io.github.aliturgutbozkurt.patterns.m01.examples.composition.before;

import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;

/**
 * Fragile base class: counts how many elements were ever added by overriding {@code add} and {@code addAll}. But
 * {@link HashSet#addAll} is implemented by calling {@code add}, so every element of {@code addAll} is counted twice.
 * The subclass depends on an implementation detail it cannot see or control.
 *
 * @see "m01 lesson, section Composition over inheritance"
 */
public final class CountingSet<E> extends HashSet<E> {

    // HashSet is Serializable, so -Xlint:serial requires an explicit version id on every subclass.
    @Serial
    private static final long serialVersionUID = 1L;

    private int addCount;

    @Override
    public boolean add(E element) {
        addCount++;
        return super.add(element);
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        addCount += elements.size();
        return super.addAll(elements);  // calls this.add(...) for each element -> counted again
    }

    public int addCount() {
        return addCount;
    }
}
