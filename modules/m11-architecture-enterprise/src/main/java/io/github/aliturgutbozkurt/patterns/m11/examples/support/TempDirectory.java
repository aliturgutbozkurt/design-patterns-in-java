package io.github.aliturgutbozkurt.patterns.m11.examples.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A temporary directory for demos that use file adapters: created on {@link #create}, deleted with everything in it
 * on {@link #close()} — use it in try-with-resources so no demo leaves files behind.
 *
 * @param path the directory (demos never print it: it differs on every run)
 * @see "m11 lesson, section Repository"
 */
public record TempDirectory(Path path) implements AutoCloseable {

    public TempDirectory {
        Objects.requireNonNull(path, "path");
    }

    /** Creates a fresh directory whose name starts with {@code prefix}. */
    public static TempDirectory create(String prefix) {
        try {
            return new TempDirectory(Files.createTempDirectory(prefix));
        } catch (IOException e) {
            throw new UncheckedIOException("cannot create a temporary directory", e);
        }
    }

    /** Deletes the directory and its contents, deepest entries first. */
    @Override
    public void close() {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> entries = Files.walk(path)) {
            List<Path> deepestFirst = entries.sorted(Comparator.reverseOrder()).toList();
            for (Path entry : deepestFirst) {
                Files.delete(entry);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("cannot delete " + path, e);
        }
    }
}
