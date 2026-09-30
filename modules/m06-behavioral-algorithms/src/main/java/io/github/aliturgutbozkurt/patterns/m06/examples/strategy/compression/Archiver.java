package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.compression;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * The Strategy context: it stores and loads data with whatever {@link Codec} it was given.
 *
 * @see "m06 lesson, section Strategy"
 */
public final class Archiver {

    private final Codec codec;

    public Archiver(Codec codec) {
        this.codec = Objects.requireNonNull(codec, "codec");
    }

    public byte[] pack(byte[] data) {
        return codec.compress().apply(data);
    }

    public byte[] unpack(byte[] packed) {
        return codec.decompress().apply(packed);
    }

    /** E.g. {@code "gzip        smaller, round trip ok"}: relative size only, because gzip bytes depend on the zlib build. */
    public String report(byte[] data) {
        byte[] packed = pack(data);
        String size = packed.length < data.length ? "smaller" : packed.length > data.length ? "larger" : "same size";
        String roundTrip = Arrays.equals(unpack(packed), data) ? "ok" : "BROKEN";
        return String.format(Locale.ROOT, "%-11s %s, round trip %s", codec.name(), size, roundTrip);
    }
}
