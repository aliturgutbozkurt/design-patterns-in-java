# Assignment 02 — Caching Proxy with TTL for a Slow Weather Service

> Module: m04-structural-wrappers · Difficulty: ★★★ · Estimated time: 3 h

## Goal

A weather service is slow and charges per request, but forecasts change only every half hour or so. Put a
**caching proxy** in front of it: clients keep calling `forecast(city)` exactly as before, and the proxy answers from
its cache while the entry is fresh. Go further than the lesson's exchange-rate example: case-insensitive keys,
failures that are *not* cached, invalidation, hit/miss statistics and a size bound that evicts the least recently
used entry.

## What you are given

- `exercises/ex02/Forecast.java` — record `(city, temperatureCelsius, summary)` — **do not modify**
- `exercises/ex02/WeatherService.java` — `Forecast forecast(String city)` — **do not modify**
- `exercises/ex02/CachingWeatherService.java` — extends `WeatherService` with `invalidate(city)` and `stats()` —
  **do not modify**
- `exercises/ex02/CacheStats.java` — record `(hits, misses)` — **do not modify**
- `exercises/ex02/TtlCachingWeatherService.java` — your code goes here (`TODO(ex02)` markers)

## Tasks

1. Constructor `TtlCachingWeatherService(WeatherService target, Duration ttl, int maxEntries, Clock clock)`: `ttl`
   must be positive and `maxEntries` ≥ 1 (else `IllegalArgumentException`); null collaborators throw
   `NullPointerException`. Read the time **only** from `clock`.
2. Cache key: the city trimmed and lower-cased with `Locale.ROOT`. A blank city throws `IllegalArgumentException`
   without calling the target.
3. An entry is fresh while `clock.instant()` is **before** `storedAt + ttl`; at exactly `storedAt + ttl` it is stale
   and is fetched again.
4. A *hit* is a call answered from the cache; a *miss* is a call delegated to the target, whether it succeeds or
   fails. Exceptions from the target propagate unchanged and are **not** cached.
5. `invalidate(city)` removes one city (unknown city: no-op).
6. When a new entry would exceed `maxEntries`, evict the **least recently used** entry (a hit or a store counts as a
   use).

## Acceptance criteria

- [ ] `firstCallGoesToTheService`
- [ ] `repeatWithinTtlIsServedFromCache`
- [ ] `entryExpiresExactlyAtTtl`
- [ ] `cityKeysIgnoreCaseAndSurroundingSpaces`
- [ ] `differentCitiesAreCachedSeparately`
- [ ] `failuresAreNotCached`
- [ ] `invalidateForcesARefresh`
- [ ] `statsCountHitsAndMisses`
- [ ] `leastRecentlyUsedEntryIsEvictedWhenFull`
- [ ] `rejectsInvalidConfiguration`
- [ ] `rejectsBlankCityWithoutCallingTheService`
- [ ] `usableWhereAWeatherServiceIsExpected`

## Run the tests

```bash
./mvnw -pl modules/m04-structural-wrappers test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — an LRU map in one line</summary>

`new LinkedHashMap<>(16, 0.75f, true)` keeps its entries in **access order**: `get` and `put` move an entry to the
end, so the first entry is always the least recently used. Override `removeEldestEntry` to return
`size() > maxEntries` and the map evicts by itself. (Use a small static nested subclass rather than an anonymous one.)

</details>

<details><summary>Hint 2 — what to store</summary>

Store the forecast **and** the instant it was stored, e.g. a private `record Entry(Forecast forecast, Instant
storedAt)`. Fresh means `now.isBefore(entry.storedAt().plus(ttl))`. Call the target first and store only if it
returned — then a failure is never cached.

</details>

<details><summary>Hint 3 — the Turkish I trap</summary>

Why `Locale.ROOT`? With a Turkish default locale, `"ISTANBUL".toLowerCase()` gives `"ıstanbul"` (dotless ı), and
`"İSTANBUL".toLowerCase(Locale.ROOT)` gives 9 characters (`i` + a combining dot) that are not equal to `"istanbul"`.
`Locale.ROOT` makes the key the same on every machine; the tests use ASCII city names so that the rule is clear.

</details>

## Stretch goals (optional, not graded)

- Make the cache safe for concurrent callers (virtual threads!) without holding a lock during the slow remote call.
  How do you stop two threads from fetching the same city at the same time? (Concurrency is the topic of m10.)
- Serve a *stale* entry when the target fails ("stale-if-error"), and count it separately in the statistics.
