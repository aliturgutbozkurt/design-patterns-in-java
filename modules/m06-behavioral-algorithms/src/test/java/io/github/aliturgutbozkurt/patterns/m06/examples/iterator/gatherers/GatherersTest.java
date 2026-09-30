package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.gatherers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherers;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class GatherersTest {

    private static List<SensorReading> readings(double... values) {
        var list = new ArrayList<SensorReading>();
        for (int i = 0; i < values.length; i++) {
            list.add(new SensorReading("t1", i * 60, values[i]));
        }
        return list;
    }

    private static final List<Click> CLICKS = List.of(
            new Click("home", 0), new Click("search", 12), new Click("product", 40),
            new Click("home", 200), new Click("cart", 215),
            new Click("checkout", 600));

    @Test
    void movingAveragesOverThreePoints() {
        assertThat(ReadingAnalytics.movingAverages(readings(10, 12, 14, 13, 30), 3)).containsExactly(12.0, 13.0, 19.0);
    }

    @Test
    void streamShorterThanTheWindowGivesOnePartialWindow() {
        assertThat(ReadingAnalytics.movingAverages(readings(10, 12), 3)).containsExactly(11.0);
    }

    @Test
    void windowFixedEndsWithAPartialWindow() {
        assertThat(ReadingAnalytics.batches(readings(1, 2, 3, 4, 5), 2))
                .containsExactly(List.of(1.0, 2.0), List.of(3.0, 4.0), List.of(5.0));
    }

    @Test
    void windowsAreUnmodifiable() {
        List<Double> first = ReadingAnalytics.batches(readings(1, 2), 2).getFirst();
        assertThatThrownBy(() -> first.add(3.0)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void scanGivesRunningTotals() {
        assertThat(ReadingAnalytics.runningTotals(readings(1, 2, 3, 4))).containsExactly(1.0, 3.0, 6.0, 10.0);
    }

    @Test
    void sessionGathererSplitsWhenTheGapIsTooLong() {
        assertThat(CLICKS.stream().gather(SessionGatherer.sessions(30)).toList()).containsExactly(
                List.of(new Click("home", 0), new Click("search", 12), new Click("product", 40)),
                List.of(new Click("home", 200), new Click("cart", 215)),
                List.of(new Click("checkout", 600)));
    }

    @Test
    void gapEqualToTheLimitStaysInTheSameSession() {
        var clicks = Stream.of(new Click("a", 0), new Click("b", 30), new Click("c", 61));
        assertThat(clicks.gather(SessionGatherer.sessions(30)).map(List::size).toList()).containsExactly(2, 1);
    }

    @Test
    void finisherEmitsTheLastSessionAndEmptyStreamEmitsNothing() {
        assertThat(Stream.of(new Click("only", 5)).gather(SessionGatherer.sessions(30)).toList())
                .containsExactly(List.of(new Click("only", 5)));
        assertThat(Stream.<Click>empty().gather(SessionGatherer.sessions(30)).toList()).isEmpty();
    }

    @Test
    void stopsEarlyOnAnInfiniteStream() {
        List<List<Click>> firstThree = Stream.iterate(0L, s -> s + 100)
                .map(s -> new Click("p", s))
                .gather(SessionGatherer.sessions(30))
                .limit(3)
                .toList();
        assertThat(firstThree).hasSize(3);
    }

    @Test
    void andThenComposesTwoGatherers() {
        assertThat(CLICKS.stream()
                .gather(SessionGatherer.sessions(30).andThen(Gatherers.scan(() -> 0, (n, s) -> n + s.size())))
                .toList()).containsExactly(3, 5, 6);
    }

    @Test
    void demoPrintsBuiltInAndCustomGatherers() {
        assertThat(Console.capture(() -> GatherersDemo.main(new String[0]))).isEqualTo("""
                values:               10.0, 12.0, 14.0, 13.0, 30.0
                3-point moving avg:   12.00, 13.00, 19.00
                windowFixed(2):       [[10.0, 12.0], [14.0, 13.0], [30.0]]
                running total (scan): [10.0, 22.0, 36.0, 49.0, 79.0]
                sessions (gap > 30 s): [[home@0, search@12, product@40], [home@200, cart@215], [checkout@600]]
                clicks so far after each session: [3, 5, 6]
                """);
    }
}
