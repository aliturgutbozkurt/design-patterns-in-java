package io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem;

/**
 * Composite, modern form: the node type is a closed set of records, so every operation can be a recursive,
 * exhaustive {@code switch} (see {@link FsOps}) instead of a method on each node class.
 *
 * @see "m05 lesson, section Composite — modern Java 27"
 */
public sealed interface FsNode permits File, Directory {

    String name();
}
