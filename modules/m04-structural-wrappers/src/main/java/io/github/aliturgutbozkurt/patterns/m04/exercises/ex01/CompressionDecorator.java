package io.github.aliturgutbozkurt.patterns.m04.exercises.ex01;

/** Assignment 01 — see assignments/01-data-source-decorators.en.md (Türkçe: 01-data-source-decorators.tr.md). */
public class CompressionDecorator extends DataSourceDecorator {

    public CompressionDecorator(DataSource wrapped) {
        super(wrapped);
    }

    @Override
    public void write(byte[] data) {
        throw new UnsupportedOperationException("TODO(ex01): GZIP-compress the data, then write it");
    }

    @Override
    public byte[] read() {
        throw new UnsupportedOperationException("TODO(ex01): read, then GZIP-decompress");
    }
}
