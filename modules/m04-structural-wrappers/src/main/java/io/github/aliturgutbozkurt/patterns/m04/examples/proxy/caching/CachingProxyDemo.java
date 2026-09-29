package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.caching;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/proxy/caching/CachingProxyDemo.java} */
public final class CachingProxyDemo {

    private CachingProxyDemo() {}

    public static void main(String[] args) {
        var remote = new SlowExchangeRateService(Map.of(
                "EUR/TRY", new BigDecimal("48.10"),
                "USD/TRY", new BigDecimal("41.25")));
        var clock = new ManualClock(Instant.parse("2026-09-29T09:00:00Z"));
        ExchangeRateService rates = new CachingExchangeRateService(remote, Duration.ofMinutes(10), clock);

        show(rates, remote, clock, "EUR", "TRY");
        clock.advance(Duration.ofMinutes(9));
        show(rates, remote, clock, "EUR", "TRY");
        show(rates, remote, clock, "USD", "TRY");
        clock.advance(Duration.ofMinutes(1));                   // exactly storedAt + ttl for EUR/TRY: stale
        show(rates, remote, clock, "EUR", "TRY");
        show(rates, remote, clock, "USD", "TRY");
    }

    private static void show(ExchangeRateService rates, SlowExchangeRateService remote, ManualClock clock,
                             String from, String to) {
        BigDecimal rate = rates.rate(from, to);
        System.out.println(clock.instant() + " " + from + "/" + to + " = " + rate + "  (remote calls: "
                + remote.calls() + ")");
    }
}
