package io.github.aliturgutbozkurt.patterns.m01.examples.composition.after;

import java.util.Collection;
import java.util.Set;

/**
 * Counts additions on top of any {@link Set}. The wrapped set's {@code addAll} calls <em>its own</em> {@code add},
 * not ours, so each element is counted exactly once.
 *
 * @see "m01 lesson, section Composition over inheritance"
 */
public final class CountingSet<E> extends ForwardingSet<E> {

    private int addCount;

    public CountingSet(Set<E> delegate) {
        super(delegate);
    }

    @Override
    public boolean add(E element) {
        addCount++;
        return super.add(element);
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        addCount += elements.size();
        return super.addAll(elements);
    }

    public int addCount() {
        return addCount;
    }
}
