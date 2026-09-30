package io.github.aliturgutbozkurt.patterns.m01.examples.composition.after;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Composition: implements {@link Set} by forwarding every call to a wrapped set. Subclasses add behaviour on top
 * without depending on how the wrapped set is implemented. (The same shape reappears as Decorator in m04.)
 *
 * @see "m01 lesson, section Composition over inheritance"
 */
public class ForwardingSet<E> implements Set<E> {

    private final Set<E> delegate;

    public ForwardingSet(Set<E> delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override public int size() { return delegate.size(); }
    @Override public boolean isEmpty() { return delegate.isEmpty(); }
    @Override public boolean contains(Object o) { return delegate.contains(o); }
    @Override public Iterator<E> iterator() { return delegate.iterator(); }
    @Override public Object[] toArray() { return delegate.toArray(); }
    @Override public <T> T[] toArray(T[] a) { return delegate.toArray(a); }
    @Override public boolean add(E e) { return delegate.add(e); }
    @Override public boolean remove(Object o) { return delegate.remove(o); }
    @Override public boolean containsAll(Collection<?> c) { return delegate.containsAll(c); }
    @Override public boolean addAll(Collection<? extends E> c) { return delegate.addAll(c); }
    @Override public boolean retainAll(Collection<?> c) { return delegate.retainAll(c); }
    @Override public boolean removeAll(Collection<?> c) { return delegate.removeAll(c); }
    @Override public void clear() { delegate.clear(); }
    @Override public boolean equals(Object o) { return o == this || delegate.equals(o); }
    @Override public int hashCode() { return delegate.hashCode(); }
    @Override public String toString() { return delegate.toString(); }
}
