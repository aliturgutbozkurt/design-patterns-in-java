package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.spliterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

/** Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/iterator/spliterator/SpliteratorDemo.java} */
public final class SpliteratorDemo {

    private static final List<String> CUSTOMERS = List.of("Ada", "Bora", "Cem", "Deniz", "Ece", "Filiz", "Gus");

    private SpliteratorDemo() {}

    public static void main(String[] args) {
        var api = new PagedCustomerSource(CUSTOMERS, 3);
        List<String> firstFour = api.stream().limit(4).toList();
        System.out.println("first 4 customers: " + firstFour + " (pages fetched: " + api.pagesFetched() + ")");

        var api2 = new PagedCustomerSource(CUSTOMERS, 3);
        System.out.println("all customers: " + api2.stream().count() + " (pages fetched: " + api2.pagesFetched() + ")");

        System.out.println("paged source: " + describe(new PagedSpliterator(api2)));
        System.out.println("int range:    " + describe(new IntRangeSpliterator(0, 1_000_000)));

        var range = new IntRangeSpliterator(0, 1_000);
        Spliterator<Integer> firstHalf = range.trySplit();
        System.out.println("trySplit of [0, 1000): " + firstHalf.estimateSize() + " + " + range.estimateSize());

        long sequential = StreamSupport.stream(new IntRangeSpliterator(0, 1_000_000), false)
                .mapToLong(Integer::longValue).sum();
        long parallel = StreamSupport.stream(new IntRangeSpliterator(0, 1_000_000), true)
                .mapToLong(Integer::longValue).sum();
        System.out.println("sum of [0, 1000000): sequential " + sequential + ", parallel " + parallel);
    }

    private static String describe(Spliterator<?> spliterator) {
        List<String> flags = new ArrayList<>();
        if (spliterator.hasCharacteristics(Spliterator.ORDERED)) {
            flags.add("ORDERED");
        }
        if (spliterator.hasCharacteristics(Spliterator.NONNULL)) {
            flags.add("NONNULL");
        }
        if (spliterator.hasCharacteristics(Spliterator.SIZED)) {
            flags.add("SIZED");
        }
        if (spliterator.hasCharacteristics(Spliterator.SUBSIZED)) {
            flags.add("SUBSIZED");
        }
        long size = spliterator.getExactSizeIfKnown();
        return String.join(" | ", flags) + ", size " + (size < 0 ? "unknown" : String.valueOf(size));
    }
}
