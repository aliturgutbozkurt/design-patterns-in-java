package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler.CrawlReport;
import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler.Crawler;
import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler.FakeWeb;
import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler.Page;
import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler.WebClient;
import io.github.aliturgutbozkurt.patterns.m10.support.Await;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class CrawlerTest {

    /** a → b, c;  b → a (cycle), d;  c → d;  d → e;  e → (nothing). */
    private static final Map<String, List<String>> SITE = Map.of(
            "a", List.of("b", "c"),
            "b", List.of("a", "d"),
            "c", List.of("d"),
            "d", List.of("e"),
            "e", List.of());

    private static CrawlReport crawl(WebClient client, int maxDepth, String start) throws InterruptedException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            return new Crawler(client, executor, maxDepth).crawl(start);
        }
    }

    @Test
    void fetchesEveryReachableUrlExactlyOnceEvenWithCycles() throws InterruptedException {
        var web = new FakeWeb(SITE, Duration.ZERO);
        var report = crawl(web, 10, "a");
        assertThat(report.visited()).containsExactly("a", "b", "c", "d", "e");
        assertThat(report.failures()).isEmpty();
        assertThat(web.fetchCounts()).containsOnlyKeys("a", "b", "c", "d", "e");
        assertThat(web.fetchCounts().values()).containsOnly(1);
    }

    @Test
    void pagesDeeperThanMaxDepthAreNotFetched() throws InterruptedException {
        var web = new FakeWeb(SITE, Duration.ZERO);
        var report = crawl(web, 2, "a");            // depth 0: a · 1: b, c · 2: d · 3: e (too deep)
        assertThat(report.visited()).containsExactly("a", "b", "c", "d");
        assertThat(web.fetchCounts()).doesNotContainKey("e");
    }

    @Test
    void depthIsTheShortestLinkDistanceFromTheStart() throws InterruptedException {
        // d is reachable at depth 2 (a → c → d) and at depth 3 (a → b → x → d): it must count as depth 2
        var site = Map.<String, List<String>>of("a", List.of("b", "c"), "b", List.of("x"), "c", List.of("d"),
                "x", List.of("d"), "d", List.of("e"), "e", List.of());
        var report = crawl(new FakeWeb(site, Duration.ZERO), 3, "a");
        assertThat(report.visited()).containsExactly("a", "b", "c", "d", "e", "x");
    }

    @Test
    void failingUrlIsReportedAndDoesNotStopTheCrawl() throws InterruptedException {
        var site = Map.<String, List<String>>of("a", List.of("broken", "b"), "b", List.of("c"), "c", List.of());
        var report = crawl(new FakeWeb(site, Duration.ZERO), 5, "a");
        assertThat(report.visited()).containsExactly("a", "b", "c");
        assertThat(report.failures()).containsExactly(Map.entry("broken", "404 Not Found: broken"));
    }

    @Test
    void reportIsSortedAndUnmodifiable() throws InterruptedException {
        var site = Map.<String, List<String>>of(
                "m", List.of("z", "b", "k"), "z", List.of(), "b", List.of(), "k", List.of("gone"));
        var report = crawl(new FakeWeb(site, Duration.ZERO), 5, "m");
        assertThat(report.visited()).containsExactly("b", "k", "m", "z");
        assertThatThrownBy(() -> report.visited().add("x")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> report.failures().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void fetchesOfOneLevelReallyRunConcurrently() throws InterruptedException {
        int n = 8;
        List<String> children = IntStream.range(0, n).mapToObj(i -> "child-" + i).toList();
        var web = new FakeWeb(Map.of("root", children), Duration.ZERO);
        var inFlightTogether = new CountDownLatch(n);
        WebClient barrierClient = url -> {
            if (url.startsWith("child-")) {
                inFlightTogether.countDown();       // releases only when all n fetches are running at once
                try {
                    if (!inFlightTogether.await(Await.BOUND.toMillis(), TimeUnit.MILLISECONDS)) {
                        throw new IOException("fetches were not in flight together");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException(e);
                }
                return new Page(url, List.of());
            }
            return web.fetch(url);
        };
        var report = crawl(barrierClient, 1, "root");
        assertThat(report.failures()).isEmpty();
        assertThat(report.visited()).hasSize(n + 1);
    }

    @Test
    void rejectsNegativeMaxDepth() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            assertThatThrownBy(() -> new Crawler(new FakeWeb(Map.of(), Duration.ZERO), executor, -1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void catalogueSiteHasTheDocumentedShape() {
        var site = FakeWeb.catalogue(5);
        assertThat(site).containsOnlyKeys(FakeWeb.productUrl(0), FakeWeb.productUrl(1), FakeWeb.productUrl(2),
                FakeWeb.productUrl(3), FakeWeb.productUrl(4));
        assertThat(site.get(FakeWeb.productUrl(1)))
                .containsExactly(FakeWeb.productUrl(3), FakeWeb.productUrl(4), FakeWeb.productUrl(0));
        assertThat(site.get(FakeWeb.productUrl(0))).contains(FakeWeb.RETIRED_URL);
        assertThat(Set.copyOf(site.get(FakeWeb.productUrl(4)))).containsExactly(FakeWeb.productUrl(0));
    }

    @Test
    void demoPrintsTheSortedReport() {
        assertThat(Demos.output(() -> CrawlerDemo.main(new String[0]))).isEqualTo("""
                crawling 200 product pages, 50 ms latency each, one virtual thread per fetch
                visited: 200 pages
                first:   https://shop.example/p/000
                last:    https://shop.example/p/199
                failures: {https://shop.example/p/retired=404 Not Found: https://shop.example/p/retired}
                every page fetched exactly once: true
                """);
    }
}
