package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.virtual;

/**
 * The expensive part: reads an image's pixels from disk or the network.
 *
 * @see "m04 lesson, section Proxy"
 */
@FunctionalInterface
public interface ImageLoader {

    /** Returns a description of the loaded pixel data. */
    String load(String fileName);
}
