package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The available compression strategies, each a matching pair of functions.
 *
 * @see "m06 lesson, section Strategy"
 */
public final class Codecs {

    private static final int MAX_RUN = 255;

    private Codecs() {}

    /** Stores the bytes unchanged; useful as a baseline and in tests. */
    public static Codec identity() {
        return new Codec("identity", bytes -> bytes.clone(), bytes -> bytes.clone());
    }

    /** Run-length encoding: each run becomes a (count, value) pair; runs longer than 255 are split. */
    public static Codec runLength() {
        return new Codec("run-length", Codecs::encodeRuns, Codecs::decodeRuns);
    }

    /** DEFLATE in the gzip format from {@code java.util.zip}. */
    public static Codec gzip() {
        return new Codec("gzip", Codecs::gzipBytes, Codecs::gunzipBytes);
    }

    private static byte[] encodeRuns(byte[] input) {
        var out = new ByteArrayOutputStream();
        int i = 0;
        while (i < input.length) {
            byte value = input[i];
            int run = 1;
            while (i + run < input.length && input[i + run] == value && run < MAX_RUN) {
                run++;
            }
            out.write(run);
            out.write(value);
            i += run;
        }
        return out.toByteArray();
    }

    private static byte[] decodeRuns(byte[] encoded) {
        if (encoded.length % 2 != 0) {
            throw new IllegalArgumentException("run-length data must have an even length: " + encoded.length);
        }
        var out = new ByteArrayOutputStream();
        for (int i = 0; i < encoded.length; i += 2) {
            int run = Byte.toUnsignedInt(encoded[i]);
            for (int j = 0; j < run; j++) {
                out.write(encoded[i + 1]);
            }
        }
        return out.toByteArray();
    }

    private static byte[] gzipBytes(byte[] input) {
        var out = new ByteArrayOutputStream();
        try (var gzip = new GZIPOutputStream(out)) {
            gzip.write(input);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return out.toByteArray();
    }

    private static byte[] gunzipBytes(byte[] compressed) {
        try (var gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            return gzip.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
