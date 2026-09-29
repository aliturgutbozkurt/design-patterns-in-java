package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.compression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

class CodecTest {

    private static final byte[] REPETITIVE = ("A".repeat(500) + "B".repeat(500)).getBytes(StandardCharsets.US_ASCII);
    private static final byte[] TEXT = "the quick brown fox jumps over the lazy dog".getBytes(StandardCharsets.US_ASCII);

    @Test
    void everyCodecRoundTrips() {
        byte[] allByteValues = new byte[256 * 3];
        for (int i = 0; i < allByteValues.length; i++) {
            allByteValues[i] = (byte) (i / 3);  // runs of three, every value from 0 to 255
        }
        for (Codec codec : List.of(Codecs.identity(), Codecs.runLength(), Codecs.gzip())) {
            for (byte[] input : List.of(REPETITIVE, TEXT, new byte[0], allByteValues)) {
                assertThat(codec.decompress().apply(codec.compress().apply(input))).as(codec.name()).isEqualTo(input);
            }
        }
    }

    @Test
    void runLengthWritesCountValuePairs() {
        byte[] packed = Codecs.runLength().compress().apply("AAAABBBCC".getBytes(StandardCharsets.US_ASCII));
        assertThat(packed).containsExactly(4, 'A', 3, 'B', 2, 'C');
    }

    @Test
    void runLengthSplitsRunsLongerThan255() {
        byte[] packed = Codecs.runLength().compress().apply("A".repeat(300).getBytes(StandardCharsets.US_ASCII));
        assertThat(packed).containsExactly((byte) 255, 'A', 45, 'A');
    }

    @Test
    void runLengthShrinksRepetitiveInputAndGrowsRandomLookingInput() {
        Codec rle = Codecs.runLength();
        assertThat(rle.compress().apply(REPETITIVE)).hasSize(8);
        assertThat(rle.compress().apply(TEXT)).hasSize(TEXT.length * 2);
    }

    @Test
    void runLengthRejectsCorruptInput() {
        assertThatIllegalArgumentException().isThrownBy(() -> Codecs.runLength().decompress().apply(new byte[] {3}));
    }

    @Test
    void gzipIsSmallerForRepetitiveInput() {
        assertThat(Codecs.gzip().compress().apply(REPETITIVE).length).isLessThan(REPETITIVE.length);
    }

    @Test
    void archiverUsesTheCodecItWasGiven() {
        UnaryOperator<byte[]> reverse = bytes -> {
            byte[] out = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                out[i] = bytes[bytes.length - 1 - i];
            }
            return out;
        };
        var archiver = new Archiver(new Codec("reverse", reverse, reverse));
        assertThat(archiver.pack(new byte[] {1, 2, 3})).containsExactly(3, 2, 1);
        assertThat(archiver.unpack(new byte[] {3, 2, 1})).containsExactly(1, 2, 3);
    }

    @Test
    void codecRequiresBothOperations() {
        assertThatNullPointerException().isThrownBy(() -> new Codec("broken", UnaryOperator.identity(), null));
    }

    @Test
    void demoPrintsSizesAndRoundTrips() {
        assertThat(Console.capture(() -> CompressionDemo.main(new String[0]))).isEqualTo("""
                run-length of "AAAABBBCC": [4, 65, 3, 66, 2, 67]
                1000 repetitive bytes:
                  identity    same size, round trip ok
                  run-length  smaller, round trip ok
                  gzip        smaller, round trip ok
                43 bytes of text:
                  identity    same size, round trip ok
                  run-length  larger, round trip ok
                  gzip        larger, round trip ok
                """);
    }
}
