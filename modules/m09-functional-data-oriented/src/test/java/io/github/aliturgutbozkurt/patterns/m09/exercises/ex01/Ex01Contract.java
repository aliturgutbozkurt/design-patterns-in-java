package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Employee.Contractor;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Employee.Hourly;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Employee.Intern;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Employee.Salaried;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.BenefitsVisitor;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacyContractor;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacyEmployee;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacyHourly;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacyIntern;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacySalaried;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.MonthlyPayVisitor;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** Assignment 01 — from Visitor to data-oriented payroll. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    protected abstract Payroll newPayroll();

    /** The fixed sample table: every kind, with and without overtime, VAT and university funding. */
    static Stream<LegacyEmployee> samples() {
        return Stream.of(
                new LegacySalaried("S-1", "Ada", 60_000_00),
                new LegacySalaried("S-2", "Alan", 50_000_05),
                new LegacyHourly("H-1", "Grace", 25_00, 120),
                new LegacyHourly("H-2", "Linus", 25_01, 170),
                new LegacyHourly("H-3", "Edsger", 30_00, 40),
                new LegacyContractor("C-1", "Barbara", 4_000_00, true),
                new LegacyContractor("C-2", "Donald", 4_000_00, false),
                new LegacyIntern("I-1", "Margaret", 900_00, false),
                new LegacyIntern("I-2", "Ken", 900_00, true));
    }

    @Test
    void convertsEveryLegacyKind() {
        var payroll = newPayroll();
        assertThat(samples().map(payroll::fromLegacy).toList()).containsExactly(
                new Salaried("S-1", "Ada", 60_000_00),
                new Salaried("S-2", "Alan", 50_000_05),
                new Hourly("H-1", "Grace", 25_00, 120),
                new Hourly("H-2", "Linus", 25_01, 170),
                new Hourly("H-3", "Edsger", 30_00, 40),
                new Contractor("C-1", "Barbara", 4_000_00, true),
                new Contractor("C-2", "Donald", 4_000_00, false),
                new Intern("I-1", "Margaret", 900_00, false),
                new Intern("I-2", "Ken", 900_00, true));
    }

    @ParameterizedTest
    @MethodSource("samples")
    void monthlyPayMatchesLegacyVisitorForAllSamples(LegacyEmployee legacy) {
        var payroll = newPayroll();
        long expected = legacy.accept(new MonthlyPayVisitor());
        assertThat(payroll.monthlyPayCents(payroll.fromLegacy(legacy))).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("samples")
    void benefitsMatchLegacyVisitorForAllSamples(LegacyEmployee legacy) {
        var payroll = newPayroll();
        assertThat(payroll.benefits(payroll.fromLegacy(legacy))).isEqualTo(legacy.accept(new BenefitsVisitor()));
    }

    @Test
    void hourlyOvertimeIsPaidAtTimeAndAHalf() {
        var payroll = newPayroll();
        assertThat(payroll.monthlyPayCents(new Hourly("H", "x", 20_00, 160))).isEqualTo(3_200_00);
        assertThat(payroll.monthlyPayCents(new Hourly("H", "x", 20_00, 170))).isEqualTo(3_200_00 + 10 * 30_00);
        assertThat(payroll.monthlyPayCents(new Hourly("H", "x", 25_01, 161))).isEqualTo(160 * 25_01 + 37_51);
    }

    @Test
    void vatAddedOnlyForRegisteredContractors() {
        var payroll = newPayroll();
        assertThat(payroll.monthlyPayCents(new Contractor("C", "x", 1_000_00, true))).isEqualTo(1_200_00);
        assertThat(payroll.monthlyPayCents(new Contractor("C", "x", 1_000_00, false))).isEqualTo(1_000_00);
    }

    @Test
    void universityFundedInternCostsNothing() {
        var payroll = newPayroll();
        assertThat(payroll.monthlyPayCents(new Intern("I", "x", 900_00, true))).isZero();
        assertThat(payroll.monthlyPayCents(new Intern("I", "x", 900_00, false))).isEqualTo(900_00);
    }

    @Test
    void kindOfClassifiesEveryVariant() {
        var payroll = newPayroll();
        assertThat(List.of(new Salaried("S", "x", 1), new Hourly("H", "x", 1, 1), new Contractor("C", "x", 1, false),
                new Intern("I", "x", 1, false))).map(payroll::kindOf)
                .containsExactly(Kind.SALARIED, Kind.HOURLY, Kind.CONTRACTOR, Kind.INTERN);
    }

    @Test
    void withRaiseReturnsNewRecordAndLeavesOriginalUnchanged() {
        var payroll = newPayroll();
        var ada = new Salaried("S-1", "Ada", 60_000_00);
        var grace = new Hourly("H-1", "Grace", 25_01, 120);
        assertThat(payroll.withRaise(ada, 10)).isEqualTo(new Salaried("S-1", "Ada", 66_000_00));
        assertThat(payroll.withRaise(grace, 3)).isEqualTo(new Hourly("H-1", "Grace", 25_76, 120));
        assertThat(payroll.withRaise(new Contractor("C", "x", 1_000_00, true), 100))
                .isEqualTo(new Contractor("C", "x", 2_000_00, true));
        assertThat(payroll.withRaise(new Intern("I", "x", 900_00, true), 0)).isEqualTo(new Intern("I", "x", 900_00, true));
        assertThat(ada).isEqualTo(new Salaried("S-1", "Ada", 60_000_00));
        assertThat(grace.hourlyRateCents()).isEqualTo(25_01);
    }

    @Test
    void withRaiseRejectsPercentOutOfRange() {
        var payroll = newPayroll();
        var ada = new Salaried("S-1", "Ada", 60_000_00);
        assertThatIllegalArgumentException().isThrownBy(() -> payroll.withRaise(ada, -1));
        assertThatIllegalArgumentException().isThrownBy(() -> payroll.withRaise(ada, 101));
    }

    @Test
    void summaryTotalsByKindIncludeAbsentKinds() {
        var payroll = newPayroll();
        var summary = payroll.summarize(List.of(new Salaried("S-1", "Ada", 12_000_00),
                new Intern("I-1", "Ken", 500_00, false), new Salaried("S-2", "Alan", 24_000_00)));
        assertThat(summary.totalCents()).isEqualTo(1_000_00 + 500_00 + 2_000_00);
        assertThat(summary.totalByKind()).isEqualTo(Map.of(
                Kind.SALARIED, 3_000_00L, Kind.HOURLY, 0L, Kind.CONTRACTOR, 0L, Kind.INTERN, 500_00L));
    }

    @Test
    void summaryListsAllHighestPaidInInputOrder() {
        var payroll = newPayroll();
        var summary = payroll.summarize(List.of(new Contractor("C-1", "Barbara", 1_000_00, true),
                new Salaried("S-1", "Ada", 12_000_00), new Salaried("S-2", "Alan", 14_400_00),
                new Hourly("H-1", "Grace", 7_50, 160)));
        assertThat(summary.highestPaidIds()).containsExactly("C-1", "S-2", "H-1");
    }

    @Test
    void summaryOfEmptyListIsZero() {
        var summary = newPayroll().summarize(List.of());
        assertThat(summary.totalCents()).isZero();
        assertThat(summary.highestPaidIds()).isEmpty();
        assertThat(summary.totalByKind()).hasSize(Kind.values().length).allSatisfy((_, total) -> assertThat(total).isZero());
    }

    @Test
    void summaryMapIsUnmodifiable() {
        var summary = newPayroll().summarize(List.of(new Salaried("S-1", "Ada", 12_000_00)));
        assertThatThrownBy(() -> summary.totalByKind().put(Kind.HOURLY, 1L))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> summary.highestPaidIds().add("X"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsNullArguments() {
        var payroll = newPayroll();
        assertThatNullPointerException().isThrownBy(() -> payroll.fromLegacy(null));
        assertThatNullPointerException().isThrownBy(() -> payroll.monthlyPayCents(null));
        assertThatNullPointerException().isThrownBy(() -> payroll.benefits(null));
        assertThatNullPointerException().isThrownBy(() -> payroll.kindOf(null));
        assertThatNullPointerException().isThrownBy(() -> payroll.withRaise(null, 5));
        assertThatNullPointerException().isThrownBy(() -> payroll.summarize(null));
    }
}
