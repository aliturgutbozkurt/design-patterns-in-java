package io.github.aliturgutbozkurt.patterns.m04.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m04.exercises.ex01.DataSource;
import java.util.Objects;

/**
 * Reference solution for assignment 01: a decorator base that encodes on the way in and decodes on the way out.
 * Subclasses supply only the two transformations (a small Template Method inside the Decorator).
 *
 * @see "m04 lesson, section Decorator"
 */
public abstract class DataSourceDecorator implements DataSource {

    private final DataSource wrapped;

    protected DataSourceDecorator(DataSource wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped, "wrapped");
    }

    @Override
    public final void write(byte[] data) {
        Objects.requireNonNull(data, "data");
        wrapped.write(data.length == 0 ? data : encode(data));    // empty stays empty
    }

    @Override
    public final byte[] read() {
        byte[] stored = wrapped.read();
        return stored.length == 0 ? stored : decode(stored);
    }

    /** Transforms data on its way to the wrapped source. */
    protected abstract byte[] encode(byte[] data);

    /** Undoes {@link #encode}; throws {@link IllegalStateException} if {@code stored} is corrupt. */
    protected abstract byte[] decode(byte[] stored);
}
