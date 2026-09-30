package io.github.aliturgutbozkurt.patterns.m01.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Assignment 01 — sales report: SRP + OCP refactoring. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    protected abstract SalesSummarizer summarizer();

    protected abstract ReportFormat textFormat();

    protected abstract ReportFormat csvFormat();

    protected abstract ReportGenerator generator(SalesSummarizer summarizer, ReportFormat format);

    private static final List<Sale> SALES = List.of(
            new Sale("Istanbul", "Laptop", new BigDecimal("2400.50")),
            new Sale("Ankara", "Monitor", new BigDecimal("1250")),
            new Sale("Izmir", "Keyboard", new BigDecimal("980.25")),
            new Sale("Istanbul", "Phone", new BigDecimal("1000")));

    private static BigDecimal money(String value) {
        return new BigDecimal(value);
    }

    @Test
    void summarizesTotalsByRegionInAlphabeticalOrder() {
        var totals = summarizer().summarize(SALES).totalsByRegion();
        assertThat(totals.keySet()).containsExactly("Ankara", "Istanbul", "Izmir");
        assertThat(totals.get("Istanbul")).isEqualTo(money("3400.50"));
    }

    @Test
    void grandTotalIsSumOfRegions() {
        assertThat(summarizer().summarize(SALES).grandTotal()).isEqualTo(money("5630.75"));
    }

    @Test
    void emptySalesGiveZeroTotal() {
        var summary = summarizer().summarize(List.of());
        assertThat(summary.totalsByRegion()).isEmpty();
        assertThat(summary.grandTotal()).isEqualTo(money("0.00"));
    }

    @Test
    void textFormatMatchesLegacyOutput() {
        var legacy = new LegacyReport();
        var service = generator(summarizer(), textFormat());
        assertThat(service.generate(SALES)).isEqualTo(legacy.generate(SALES, "text")).isEqualTo("""
                SALES BY REGION
                Ankara         1250.00
                Istanbul       3400.50
                Izmir           980.25
                TOTAL          5630.75
                """);
        assertThat(service.generate(List.of())).isEqualTo(legacy.generate(List.of(), "text"));
    }

    @Test
    void csvFormatMatchesLegacyOutput() {
        var legacy = new LegacyReport();
        var service = generator(summarizer(), csvFormat());
        assertThat(service.generate(SALES)).isEqualTo(legacy.generate(SALES, "csv"));
        assertThat(service.generate(List.of())).isEqualTo(legacy.generate(List.of(), "csv"));
    }

    @Test
    void newFormatPlugsInWithoutChangingService() {
        ReportFormat oneLine = summary -> summary.totalsByRegion().size() + " regions, total " + summary.grandTotal();
        assertThat(generator(summarizer(), oneLine).generate(SALES)).isEqualTo("3 regions, total 5630.75");
    }

    @Test
    void amountsUseScaleTwo() {
        var summary = summarizer().summarize(List.of(
                new Sale("Bursa", "Pen", new BigDecimal("10")), new Sale("Bursa", "Ink", new BigDecimal("0.5"))));
        assertThat(summary.totalsByRegion().get("Bursa")).isEqualTo(money("10.50"));
        assertThat(summary.grandTotal().scale()).isEqualTo(2);
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> summarizer().summarize(null));
        assertThatNullPointerException().isThrownBy(() -> generator(null, textFormat()));
        assertThatNullPointerException().isThrownBy(() -> generator(summarizer(), null));
        assertThatNullPointerException().isThrownBy(() -> generator(summarizer(), textFormat()).generate(null));
    }
}
