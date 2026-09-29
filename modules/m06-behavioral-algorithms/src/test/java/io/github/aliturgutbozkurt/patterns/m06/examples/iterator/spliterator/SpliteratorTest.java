package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.spliterator;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;

class SpliteratorTest {

    private static final List<String> CUSTOMERS = List.of("Ada", "Bora", "Cem", "Deniz", "Ece", "Filiz", "Gus");

    @Test
    void limitFetchesOnlyThePagesItNeeds() {
        var api = new PagedCustomerSource(CUSTOMERS, 3);
        assertThat(api.stream().limit(4).toList()).containsExactly("Ada", "Bora", "Cem", "Deniz");
        assertThat(api.pagesFetched()).isEqualTo(2);
    }

    @Test
    void nothingIsFetchedUntilTheStreamRuns() {
        var api = new PagedCustomerSource(CUSTOMERS, 3);
        var stream = api.stream().map(String::toUpperCase);
        assertThat(api.pagesFetched()).isZero();
        assertThat(stream.toList()).hasSize(7);
        assertThat(api.pagesFetched()).isEqualTo(3);
    }

    @Test
    void fullLastPageNeedsOneMoreEmptyFetch() {
        var api = new PagedCustomerSource(CUSTOMERS.subList(0, 6), 3);
        assertThat(api.stream().toList()).hasSize(6);
        assertThat(api.pagesFetched()).isEqualTo(3);
    }

    @Test
    void characteristicsDescribeEachSource() {
        var paged = new PagedSpliterator(new PagedCustomerSource(CUSTOMERS, 3));
        assertThat(paged.characteristics()).isEqualTo(Spliterator.ORDERED | Spliterator.NONNULL);
        assertThat(paged.getExactSizeIfKnown()).isEqualTo(-1);
        var range = new IntRangeSpliterator(0, 10);
        assertThat(range.characteristics()).isEqualTo(Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED);
        assertThat(range.getExactSizeIfKnown()).isEqualTo(10);
    }

    @Test
    void trySplitHalvesTheRange() {
        var range = new IntRangeSpliterator(0, 1_000);
        Spliterator<Integer> firstHalf = range.trySplit();
        assertThat(firstHalf.estimateSize()).isEqualTo(500);
        assertThat(range.estimateSize()).isEqualTo(500);
        assertThat(StreamSupport.stream(firstHalf, false).findFirst()).contains(0);
        assertThat(StreamSupport.stream(range, false).findFirst()).contains(500);
        assertThat(new IntRangeSpliterator(0, 1).trySplit()).isNull();
    }

    @Test
    void parallelSumEqualsSequentialSum() {
        long sequential = StreamSupport.stream(new IntRangeSpliterator(0, 100_000), false)
                .mapToLong(Integer::longValue).sum();
        long parallel = StreamSupport.stream(new IntRangeSpliterator(0, 100_000), true)
                .mapToLong(Integer::longValue).sum();
        assertThat(parallel).isEqualTo(sequential).isEqualTo(4_999_950_000L);
    }

    @Test
    void parallelStreamKeepsEncounterOrder() {
        assertThat(StreamSupport.stream(new IntRangeSpliterator(0, 1_000), true).toList())
                .isEqualTo(StreamSupport.stream(new IntRangeSpliterator(0, 1_000), false).toList());
    }

    @Test
    void demoPrintsLazinessCharacteristicsAndSums() {
        assertThat(Console.capture(() -> SpliteratorDemo.main(new String[0]))).isEqualTo("""
                first 4 customers: [Ada, Bora, Cem, Deniz] (pages fetched: 2)
                all customers: 7 (pages fetched: 3)
                paged source: ORDERED | NONNULL, size unknown
                int range:    ORDERED | SIZED | SUBSIZED, size 1000000
                trySplit of [0, 1000): 500 + 500
                sum of [0, 1000000): sequential 499999500000, parallel 499999500000
                """);
    }
}
