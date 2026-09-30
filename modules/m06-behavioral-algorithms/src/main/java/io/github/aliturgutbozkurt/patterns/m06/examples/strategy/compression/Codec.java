package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.compression;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * A strategy with two operations that must match: {@code decompress(compress(x))} equals {@code x}. Keeping the pair in
 * one record means nobody can combine gzip compression with run-length decompression by mistake.
 *
 * @see "m06 lesson, section Strategy"
 */
public record Codec(String name, UnaryOperator<byte[]> compress, UnaryOperator<byte[]> decompress) {

    public Codec {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(compress, "compress");
        Objects.requireNonNull(decompress, "decompress");
    }
}
