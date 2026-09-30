package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.virtual;

import java.util.Objects;

/**
 * Real subject: loads its pixels as soon as it is constructed — expensive.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class HighResImage implements Image {

    private final String fileName;
    private final String pixels;

    public HighResImage(String fileName, ImageLoader loader) {
        this.fileName = Objects.requireNonNull(fileName, "fileName");
        this.pixels = loader.load(fileName);                    // the expensive work happens here
    }

    @Override
    public String fileName() {
        return fileName;
    }

    @Override
    public String render() {
        return fileName + " [" + pixels + "]";
    }
}
