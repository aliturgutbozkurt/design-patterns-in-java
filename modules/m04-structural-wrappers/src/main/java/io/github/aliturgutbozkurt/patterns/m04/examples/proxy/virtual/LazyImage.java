package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.virtual;

import java.util.Objects;

/**
 * Virtual proxy: stands in for a {@link HighResImage} and creates it only when it is really rendered, at most once.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class LazyImage implements Image {

    private final String fileName;
    private final Lazy<HighResImage> real;

    public LazyImage(String fileName, ImageLoader loader) {
        this.fileName = Objects.requireNonNull(fileName, "fileName");
        Objects.requireNonNull(loader, "loader");
        this.real = Lazy.of(() -> new HighResImage(fileName, loader));
    }

    @Override
    public String fileName() {
        return fileName;                                        // cheap question: answered without loading
    }

    @Override
    public String render() {
        return real.get().render();                             // first call loads, later calls reuse
    }

    public boolean isLoaded() {
        return real.isInitialized();
    }
}
