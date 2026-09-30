package io.github.aliturgutbozkurt.patterns.m04.exercises.ex02;

/**
 * GIVEN — do not modify. A <em>hit</em> is a call answered from the cache; a <em>miss</em> is a call delegated to
 * the real service, whether it succeeds or fails.
 */
public record CacheStats(long hits, long misses) {}
