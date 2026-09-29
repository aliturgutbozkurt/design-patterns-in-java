package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.jdk;

import java.io.InputStream;

/**
 * {@link InputStream} is a template method class too: implement the single-byte {@code read()}, and
 * {@code read(byte[], int, int)}, {@code readAllBytes} and {@code transferTo} are built on top of it.
 * This stream yields the first {@code letters} lowercase letters.
 *
 * @see "m06 lesson, section Template Method"
 */
public final class AlphabetStream extends InputStream {

    private final int letters;
    private int next;

    public AlphabetStream(int letters) {
        if (letters < 0 || letters > 26) {
            throw new IllegalArgumentException("letters must be 0..26: " + letters);
        }
        this.letters = letters;
    }

    @Override
    public int read() {
        return next < letters ? 'a' + next++ : -1;
    }
}
