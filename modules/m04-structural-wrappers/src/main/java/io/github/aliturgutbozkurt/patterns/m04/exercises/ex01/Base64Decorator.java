package io.github.aliturgutbozkurt.patterns.m04.exercises.ex01;

/** Assignment 01 — see assignments/01-data-source-decorators.en.md (Türkçe: 01-data-source-decorators.tr.md). */
public class Base64Decorator extends DataSourceDecorator {

    public Base64Decorator(DataSource wrapped) {
        super(wrapped);
    }

    @Override
    public void write(byte[] data) {
        throw new UnsupportedOperationException("TODO(ex01): encode the data as standard Base64 (US-ASCII bytes), then write it");
    }

    @Override
    public byte[] read() {
        throw new UnsupportedOperationException("TODO(ex01): read, then decode the Base64 text");
    }
}
