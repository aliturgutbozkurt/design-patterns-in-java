package io.github.aliturgutbozkurt.patterns.m04.exercises.ex01;

/** GIVEN — do not modify. Somewhere bytes can be written to and read back from (a file, a blob, a column). */
public interface DataSource {

    /** Replaces the stored data with {@code data}. */
    void write(byte[] data);

    /** The data last written, or an empty array if nothing was written yet. */
    byte[] read();
}
