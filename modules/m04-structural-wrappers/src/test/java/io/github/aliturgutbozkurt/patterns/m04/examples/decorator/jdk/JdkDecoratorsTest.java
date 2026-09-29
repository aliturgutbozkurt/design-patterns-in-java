package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.jdk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.api.Test;

class JdkDecoratorsTest {

    private static final byte[] TEN_BYTES = "0123456789".getBytes(StandardCharsets.US_ASCII);

    /** A source that remembers whether it was closed. */
    private static final class TrackingSource extends ByteArrayInputStream {
        boolean closed;

        TrackingSource(byte[] bytes) {
            super(bytes);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }

    @Test
    void gzipRoundTripThroughWriterAndReaderStacksGivesTheOriginalLines() throws IOException {
        List<String> original = List.of("first line", "çğıİöşü", "", "last line");
        byte[] gzip = JdkDecoratorsDemo.compress(original);
        assertThat(gzip[0]).isEqualTo((byte) 0x1f);
        assertThat(gzip[1]).isEqualTo((byte) 0x8b);
        try (var reader = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(new ByteArrayInputStream(gzip)), StandardCharsets.UTF_8))) {
            assertThat(reader.lines().toList()).isEqualTo(original);
        }
    }

    @Test
    void countsBytesReadOneByOneAndInBlocks() throws IOException {
        try (var in = new CountingInputStream(new ByteArrayInputStream(TEN_BYTES))) {
            in.read();
            in.read();
            assertThat(in.count()).isEqualTo(2);
            in.read(new byte[3]);                                  // read(byte[]) goes through read(byte[], int, int)
            assertThat(in.count()).isEqualTo(5);
            in.read(new byte[10], 0, 4);
            assertThat(in.count()).isEqualTo(9);
            in.read(new byte[10], 0, 10);                           // only 1 byte left
            assertThat(in.count()).isEqualTo(10);
            assertThat(in.read()).isEqualTo(-1);
            assertThat(in.read(new byte[4], 0, 4)).isEqualTo(-1);
            assertThat(in.count()).isEqualTo(10);
        }
    }

    @Test
    void countsSkippedBytes() throws IOException {
        try (var in = new CountingInputStream(new ByteArrayInputStream(TEN_BYTES))) {
            assertThat(in.skip(4)).isEqualTo(4);
            assertThat(in.count()).isEqualTo(4);
            assertThat(in.readAllBytes()).hasSize(6);
            assertThat(in.count()).isEqualTo(10);
        }
    }

    @Test
    void doesNotSupportMarkAndReset() throws IOException {
        try (var in = new CountingInputStream(new ByteArrayInputStream(TEN_BYTES))) {
            assertThat(in.markSupported()).isFalse();
            in.mark(5);
            assertThatIOException().isThrownBy(in::reset);
        }
    }

    @Test
    void closingTheOutermostReaderClosesTheInnermostStream() throws IOException {
        var source = new TrackingSource(JdkDecoratorsDemo.compress(List.of("x")));
        var reader = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(new CountingInputStream(source)), StandardCharsets.UTF_8));
        assertThat(source.closed).isFalse();
        reader.close();
        assertThat(source.closed).isTrue();
    }

    @Test
    void unmodifiableViewShowsLaterChangesButACopyDoesNot() {
        List<String> backing = new ArrayList<>(List.of("a", "b"));
        List<String> view = Collections.unmodifiableList(backing);
        List<String> copy = List.copyOf(backing);
        backing.add("c");
        assertThat(view).containsExactly("a", "b", "c");
        assertThat(copy).containsExactly("a", "b");
        assertThatThrownBy(() -> view.add("d")).isInstanceOf(UnsupportedOperationException.class);
        assertThat(Collections.unmodifiableList(view)).isSameAs(view);   // not wrapped twice
    }

    @Test
    void demoPrintsTheDecoratorStackAndTheViewVersusCopy() {
        assertThat(Console.capture(() -> JdkDecoratorsDemo.main(new String[0]))).isEqualTo("""
                wrote 4 lines -> 92 gzip bytes
                read back 4 lines, errors: [ERROR payment timeout]
                CountingInputStream saw 92 of 92 bytes
                after backing.add("c"): view [a, b, c], copy [a, b]
                view.add -> UnsupportedOperationException
                """);
    }
}
