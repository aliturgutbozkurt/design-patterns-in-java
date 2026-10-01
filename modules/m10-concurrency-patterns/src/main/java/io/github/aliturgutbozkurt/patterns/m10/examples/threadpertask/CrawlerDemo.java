package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask;

import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler.CrawlReport;
import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler.Crawler;
import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler.FakeWeb;
import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/threadpertask/CrawlerDemo.java}
 *
 * <p>Crawls a 200-page fake shop with 50 ms latency per page. Fetched one after another that would take 10 s; with
 * one virtual thread per fetch it takes about one latency per level. The output is the sorted report only.
 */
public final class CrawlerDemo {

    private CrawlerDemo() {}

    public static void main(String[] args) throws InterruptedException {
        var web = new FakeWeb(FakeWeb.catalogue(200), Duration.ofMillis(50));
        System.out.println("crawling 200 product pages, 50 ms latency each, one virtual thread per fetch");

        CrawlReport report;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {   // close() waits for all tasks
            report = new Crawler(web, executor, 10).crawl(FakeWeb.productUrl(0));
        }

        System.out.println("visited: " + report.visited().size() + " pages");
        System.out.println("first:   " + report.visited().first());
        System.out.println("last:    " + report.visited().last());
        System.out.println("failures: " + report.failures());
        System.out.println("every page fetched exactly once: "
                + web.fetchCounts().values().stream().allMatch(count -> count == 1));
    }
}
