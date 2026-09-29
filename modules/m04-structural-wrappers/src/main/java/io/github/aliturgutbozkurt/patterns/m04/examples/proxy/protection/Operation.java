package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.protection;

/**
 * The operations of a {@link DocumentStore}, so a permission rule can name them.
 *
 * @see "m04 lesson, section Proxy"
 */
public enum Operation {
    READ,
    WRITE,
    DELETE
}
