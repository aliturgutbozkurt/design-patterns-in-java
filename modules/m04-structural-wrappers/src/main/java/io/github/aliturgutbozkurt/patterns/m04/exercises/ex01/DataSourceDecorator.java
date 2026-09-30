package io.github.aliturgutbozkurt.patterns.m04.exercises.ex01;

/** Assignment 01 — see assignments/01-data-source-decorators.en.md (Türkçe: 01-data-source-decorators.tr.md). */
public abstract class DataSourceDecorator implements DataSource {

    protected DataSourceDecorator(DataSource wrapped) {
        // TODO(ex01): keep the wrapped source (reject null); the decorator holds no data of its own.
    }

    @Override
    public void write(byte[] data) {
        throw new UnsupportedOperationException("TODO(ex01): transform the data, then write it to the wrapped source");
    }

    @Override
    public byte[] read() {
        throw new UnsupportedOperationException("TODO(ex01): read from the wrapped source, then undo the transformation");
    }
}
