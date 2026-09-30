package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.jdk;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A custom {@code java.io} decorator: counts the bytes consumed from the wrapped stream.
 *
 * @see "m04 lesson, section Decorator"
 */
public final class CountingInputStream extends FilterInputStream {

    private long count;

    public CountingInputStream(InputStream in) {
        super(Objects.requireNonNull(in, "in"));
    }

    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b != -1) {
            count++;
        }
        return b;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {   // read(byte[]) comes here too
        int n = super.read(buffer, offset, length);
        if (n > 0) {
            count += n;
        }
        return n;
    }

    @Override
    public long skip(long n) throws IOException {       // FilterInputStream.skip bypasses the read overrides
        long skipped = super.skip(n);
        count += skipped;
        return skipped;
    }

    @Override
    public boolean markSupported() {                    // reset() would make the count lie
        return false;
    }

    @Override
    public void mark(int readLimit) {
        // mark/reset is not supported (see markSupported), so there is nothing to remember
    }

    @Override
    public void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    /** Bytes consumed so far through {@code read} and {@code skip}. */
    public long count() {
        return count;
    }
}
