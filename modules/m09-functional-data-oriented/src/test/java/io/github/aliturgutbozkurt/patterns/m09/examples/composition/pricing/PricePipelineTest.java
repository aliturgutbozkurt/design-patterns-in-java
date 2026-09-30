package io.github.aliturgutbozkurt.patterns.m09.examples.composition.pricing;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PricePipelineTest {

    private static final Function<Price, Price> TEN_OFF = PriceRules.percentOff(10);
    private static final Function<Price, Price> TAX = PriceRules.addTax(20);

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 99, 10_000, 12_345})
    void andThenEqualsComposeTheOtherWayRound(long cents) {
        var price = new Price(cents);
        assertThat(TEN_OFF.andThen(TAX).apply(price)).isEqualTo(TAX.compose(TEN_OFF).apply(price));
        assertThat(TAX.andThen(TEN_OFF).apply(price)).isEqualTo(TEN_OFF.compose(TAX).apply(price));
    }

    @Test
    void orderMatters() {
        var price = new Price(100_00);
        assertThat(PriceRules.amountOff(10_00).andThen(TAX).apply(price)).isEqualTo(new Price(108_00));
        assertThat(TAX.andThen(PriceRules.amountOff(10_00)).apply(price)).isEqualTo(new Price(110_00));
    }

    @ParameterizedTest
    @CsvSource({"10000, 9000", "999, 899", "5, 4"})
    void percentOffRoundsDownToWholeCents(long before, long after) {
        assertThat(TEN_OFF.apply(new Price(before))).isEqualTo(new Price(after));
    }

    @Test
    void anEmptyPipelineIsTheIdentity() {
        assertThat(PricePipeline.of(List.of()).apply(new Price(42_00))).isEqualTo(new Price(42_00));
    }

    @Test
    void thePipelineAppliesRulesInListOrder() {
        var trace = new ArrayList<String>();
        Function<Price, Price> a = p -> {
            trace.add("a(" + p.cents() + ")");
            return new Price(p.cents() + 1);
        };
        Function<Price, Price> b = p -> {
            trace.add("b(" + p.cents() + ")");
            return new Price(p.cents() * 10);
        };
        assertThat(PricePipeline.of(List.of(a, b, a)).apply(new Price(1))).isEqualTo(new Price(21));
        assertThat(trace).containsExactly("a(1)", "b(2)", "a(20)");
    }

    @Test
    void floorAtStopsAPriceGoingBelowTheFloor() {
        var rules = PricePipeline.of(List.of(PriceRules.amountOff(50_00), PriceRules.floorAt(10_00)));
        assertThat(rules.apply(new Price(55_00))).isEqualTo(new Price(10_00));
        assertThat(rules.apply(new Price(80_00))).isEqualTo(new Price(30_00));
        assertThat(PriceRules.amountOff(50_00).apply(new Price(20_00))).isEqualTo(new Price(0));
    }

    @Test
    void combinedPredicatesGiveTheExpectedEligibility() {
        var ada = new Customer("Ada", true, 0, false);
        var grace = new Customer("Grace", false, 7, false);
        var linus = new Customer("Linus", false, 1, false);
        var mallory = new Customer("Mallory", true, 12, true);
        Predicate<Customer> freeShipping = Eligibility.freeShipping();
        assertThat(List.of(ada, grace, linus, mallory)).map(freeShipping::test)
                .containsExactly(true, true, false, false);
        assertThat(List.of(ada, grace, linus, mallory)).map(Eligibility.newcomer()::test)
                .containsExactly(true, false, true, false);
        assertThat(Eligibility.member().negate().test(grace)).isTrue();
    }

    @Test
    void demoPrintsThePipelineAndTheEligibilityTable() {
        assertThat(Console.capture(() -> PricePipelineDemo.main(new String[0]))).isEqualTo("""
                -- one rule at a time, composed with andThen
                100.00 -> 10% off -> 90.00 -> +20% tax -> 108.00
                -- order matters
                amountOff(10.00).andThen(addTax(20)) = 108.00
                addTax(20).andThen(amountOff(10.00)) = 110.00
                -- a list of rules folded into one function (reduce(identity, andThen))
                spring sale on  40.00 = 10.00
                spring sale on 120.00 = 66.00
                -- eligibility rules combined with and / or / not
                Ada      freeShipping=true  newcomer=true
                Grace    freeShipping=true  newcomer=false
                Linus    freeShipping=false newcomer=true
                Mallory  freeShipping=false newcomer=false
                """);
    }
}
