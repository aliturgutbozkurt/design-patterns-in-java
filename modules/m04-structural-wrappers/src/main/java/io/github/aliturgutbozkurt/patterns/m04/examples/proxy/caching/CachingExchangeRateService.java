package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.caching;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Caching proxy: remembers each rate for a time-to-live and asks the real service only when the entry is missing or
 * stale. Single-threaded by design (thread-safe caches come in m10).
 *
 * @see "m04 lesson, section Proxy"
 */
public final class CachingExchangeRateService implements ExchangeRateService {

    private record Entry(BigDecimal rate, Instant storedAt) {}

    private final ExchangeRateService target;
    private final Duration ttl;
    private final Clock clock;
    private final Map<String, Entry> cache = new HashMap<>();

    public CachingExchangeRateService(ExchangeRateService target, Duration ttl, Clock clock) {
        this.target = Objects.requireNonNull(target, "target");
        this.clock = Objects.requireNonNull(clock, "clock");
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("ttl must be positive: " + ttl);
        }
        this.ttl = ttl;
    }

    @Override
    public BigDecimal rate(String from, String to) {
        String key = from + "/" + to;
        Instant now = clock.instant();
        Entry entry = cache.get(key);
        if (entry != null && now.isBefore(entry.storedAt().plus(ttl))) {     // fresh until storedAt + ttl
            return entry.rate();
        }
        BigDecimal rate = target.rate(from, to);
        cache.put(key, new Entry(rate, now));
        return rate;
    }
}
