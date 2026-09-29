package io.github.aliturgutbozkurt.patterns.m04.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. The "file store": keeps a defensive copy of the last write. */
public final class InMemoryDataSource implements DataSource {

    private byte[] data = new byte[0];

    @Override
    public void write(byte[] data) {
        this.data = Objects.requireNonNull(data, "data").clone();
    }

    @Override
    public byte[] read() {
        return data.clone();
    }
}
