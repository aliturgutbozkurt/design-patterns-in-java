package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.virtual;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fake loader: pretends to read a 24-megapixel file and counts how often it was asked (thread-safe).
 *
 * @see "m04 lesson, section Proxy"
 */
public final class CountingImageLoader implements ImageLoader {

    private final AtomicInteger loads = new AtomicInteger();

    @Override
    public String load(String fileName) {
        loads.incrementAndGet();
        return "6000x4000 pixels";
    }

    public int loads() {
        return loads.get();
    }
}
