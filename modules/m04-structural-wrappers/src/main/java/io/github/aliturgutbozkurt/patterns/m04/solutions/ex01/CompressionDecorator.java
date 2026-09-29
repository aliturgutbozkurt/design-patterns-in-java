package io.github.aliturgutbozkurt.patterns.m04.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m04.exercises.ex01.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Reference solution for assignment 01: GZIP-compresses on write, decompresses on read.
 *
 * @see "m04 lesson, section Decorator"
 */
public class CompressionDecorator extends DataSourceDecorator {

    public CompressionDecorator(DataSource wrapped) {
        super(wrapped);
    }

    @Override
    protected byte[] encode(byte[] data) {
        var bytes = new ByteArrayOutputStream();
        try (var gzip = new GZIPOutputStream(bytes)) {
            gzip.write(data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);                 // cannot happen with an in-memory stream
        }
        return bytes.toByteArray();
    }

    @Override
    protected byte[] decode(byte[] stored) {
        try (var gzip = new GZIPInputStream(new ByteArrayInputStream(stored))) {
            return gzip.readAllBytes();
        } catch (IOException e) {                              // ZipException, EOFException: corrupt data
            throw new IllegalStateException("corrupt compressed data", e);
        }
    }
}
