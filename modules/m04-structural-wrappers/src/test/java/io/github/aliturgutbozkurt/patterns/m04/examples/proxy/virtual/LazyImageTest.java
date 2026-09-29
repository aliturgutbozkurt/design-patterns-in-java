package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.virtual;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

class LazyImageTest {

    @Test
    void creatingProxiesLoadsNothing() {
        var loader = new CountingImageLoader();
        var image = new LazyImage("a.jpg", loader);
        assertThat(loader.loads()).isZero();
        assertThat(image.isLoaded()).isFalse();
    }

    @Test
    void fileNameDoesNotLoad() {
        var loader = new CountingImageLoader();
        assertThat(new LazyImage("a.jpg", loader).fileName()).isEqualTo("a.jpg");
        assertThat(loader.loads()).isZero();
    }

    @Test
    void firstRenderLoadsExactlyOnceAndLaterCallsReuseIt() {
        var loader = new CountingImageLoader();
        var image = new LazyImage("a.jpg", loader);
        assertThat(image.render()).isEqualTo("a.jpg [6000x4000 pixels]");
        assertThat(image.render()).isEqualTo("a.jpg [6000x4000 pixels]");
        assertThat(loader.loads()).isEqualTo(1);
        assertThat(image.isLoaded()).isTrue();
    }

    @Test
    void galleryOfThreeWhereOneIsViewedLoadsOneImage() {
        var loader = new CountingImageLoader();
        List<Image> gallery = List.of(
                new LazyImage("a.jpg", loader), new LazyImage("b.jpg", loader), new LazyImage("c.jpg", loader));
        gallery.forEach(Image::fileName);
        gallery.get(2).render();
        assertThat(loader.loads()).isEqualTo(1);
    }

    @Test
    void proxyAndRealImageRenderTheSame() {
        var loader = new CountingImageLoader();
        Image real = new HighResImage("a.jpg", loader);
        Image proxy = new LazyImage("a.jpg", loader);
        assertThat(proxy.render()).isEqualTo(real.render());
    }

    @Test
    void thousandVirtualThreadsRenderingTheSameProxyLoadItExactlyOnce() {
        var loader = new CountingImageLoader();
        Image image = new LazyImage("a.jpg", loader);
        var results = new ConcurrentLinkedQueue<String>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                executor.submit(() -> results.add(image.render()));
            }
        }
        assertThat(results).hasSize(1_000).containsOnly("a.jpg [6000x4000 pixels]");
        assertThat(loader.loads()).isEqualTo(1);
    }

    @Test
    void lazyComputesOnceAndRejectsNull() {
        var calls = new int[1];
        Lazy<String> lazy = Lazy.of(() -> "value " + ++calls[0]);
        assertThat(lazy.isInitialized()).isFalse();
        assertThat(lazy.get()).isEqualTo("value 1");
        assertThat(lazy.get()).isEqualTo("value 1");
        assertThat(lazy.isInitialized()).isTrue();
        assertThatNullPointerException().isThrownBy(() -> Lazy.of(() -> null).get());
        assertThatNullPointerException().isThrownBy(() -> Lazy.of(null));
    }

    @Test
    void demoPrintsLoadCounts() {
        assertThat(Console.capture(() -> LazyImageDemo.main(new String[0]))).isEqualTo("""
                gallery of 3 created, loads: 0
                thumbnails: [beach.jpg, bosphorus.jpg, cappadocia.jpg], loads: 0
                open: bosphorus.jpg [6000x4000 pixels]
                open again: bosphorus.jpg [6000x4000 pixels]
                loads after viewing one image twice: 1
                1000 virtual threads rendered one proxy, loads: 1
                """);
    }
}
