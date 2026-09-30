package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.logs;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * How many lines each level had. Every level is present (zero if unseen), so two counts of the same input are
 * equal no matter how the lines were split between consumers.
 *
 * @see "m10 lesson, section Producer–Consumer"
 */
public record LevelCounts(SortedMap<Level, Long> counts) {

    public LevelCounts {
        SortedMap<Level, Long> all = new TreeMap<>();
        for (Level level : Level.values()) {
            long count = counts.getOrDefault(level, 0L);
            if (count < 0) {
                throw new IllegalArgumentException("negative count for " + level + ": " + count);
            }
            all.put(level, count);
        }
        counts = Collections.unmodifiableSortedMap(all);
    }

    /** Counts from a mutable tally, e.g. a consumer's private {@link EnumMap}. */
    public static LevelCounts fromTally(Map<Level, Long> tally) {
        return new LevelCounts(new TreeMap<>(tally));
    }

    /** The reference result: the same lines counted on one thread. */
    public static LevelCounts countSequentially(Iterable<String> lines) {
        Map<Level, Long> tally = new EnumMap<>(Level.class);
        for (String line : lines) {
            tally.merge(Level.of(line), 1L, Long::sum);
        }
        return fromTally(tally);
    }

    public long of(Level level) {
        return counts.get(level);
    }

    public long total() {
        return counts.values().stream().mapToLong(Long::longValue).sum();
    }

    /** Adds two partial counts (used to combine what the consumers counted). */
    public LevelCounts merge(LevelCounts other) {
        Map<Level, Long> sum = new EnumMap<>(counts);
        other.counts.forEach((level, count) -> sum.merge(level, count, Long::sum));
        return fromTally(sum);
    }
}
