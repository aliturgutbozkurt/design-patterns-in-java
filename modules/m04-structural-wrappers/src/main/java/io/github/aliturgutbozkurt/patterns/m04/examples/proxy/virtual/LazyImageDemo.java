package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.virtual;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/proxy/virtual/LazyImageDemo.java}
 *
 * @see "m04 lesson, section Proxy"
 */
public final class LazyImageDemo {

    private LazyImageDemo() {}

    public static void main(String[] args) {
        var loader = new CountingImageLoader();
        List<Image> gallery = List.of(
                new LazyImage("beach.jpg", loader),
                new LazyImage("bosphorus.jpg", loader),
                new LazyImage("cappadocia.jpg", loader));
        System.out.println("gallery of " + gallery.size() + " created, loads: " + loader.loads());

        System.out.println("thumbnails: " + gallery.stream().map(Image::fileName).toList()
                + ", loads: " + loader.loads());

        Image opened = gallery.get(1);
        System.out.println("open: " + opened.render());
        System.out.println("open again: " + opened.render());
        System.out.println("loads after viewing one image twice: " + loader.loads());

        var sharedLoader = new CountingImageLoader();
        Image shared = new LazyImage("galata.jpg", sharedLoader);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                executor.submit(shared::render);
            }
        }
        System.out.println("1000 virtual threads rendered one proxy, loads: " + sharedLoader.loads());
    }
}
