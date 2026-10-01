package io.github.aliturgutbozkurt.patterns.m09.examples.composition.currying;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m09.examples.composition.currying.LogFormat.Level;
import io.github.aliturgutbozkurt.patterns.m09.examples.composition.currying.ShippingRates.Zone;
import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CurryingTest {

    private static final BiFunction<Integer, Integer, Integer> MINUS = (a, b) -> a - b;

    @ParameterizedTest
    @CsvSource({"10, 3", "3, 10", "0, 0", "-4, 7"})
    void uncurryOfCurryBehavesLikeTheOriginal(int a, int b) {
        assertThat(Curry.uncurry(Curry.curry(MINUS)).apply(a, b)).isEqualTo(MINUS.apply(a, b));
        assertThat(Curry.curry(MINUS).apply(a).apply(b)).isEqualTo(MINUS.apply(a, b));
    }

    @ParameterizedTest
    @CsvSource({"10, 3", "3, 10", "-4, 7"})
    void partialFixesTheFirstArgument(int a, int b) {
        assertThat(Curry.partial(MINUS, a).apply(b)).isEqualTo(MINUS.apply(a, b));
    }

    @Test
    void flipSwapsTheArguments() {
        assertThat(Curry.flip(MINUS).apply(10, 3)).isEqualTo(-7);
        assertThat(Curry.flip(Curry.flip(MINUS)).apply(10, 3)).isEqualTo(7);
    }

    @ParameterizedTest
    @CsvSource({
            "DOMESTIC, 1, 499", "DOMESTIC, 1000, 499", "DOMESTIC, 1001, 599", "DOMESTIC, 2000, 599",
            "DOMESTIC, 2001, 699", "EU, 1000, 999", "EU, 1001, 1249", "WORLD, 1000, 1999", "WORLD, 3500, 3499"})
    void ratesAreExactAtTheWeightBoundaries(Zone zone, int grams, long cents) {
        assertThat(ShippingRates.rates().apply(zone).apply(grams)).isEqualTo(cents);
    }

    @Test
    void aZoneTariffIsStoredAndReusedAsAFunction() {
        Function<Integer, Long> domestic = ShippingRates.forZone(Zone.DOMESTIC);
        assertThat(domestic.apply(500)).isEqualTo(499L);
        assertThat(domestic.apply(1500)).isEqualTo(599L);
        assertThat(domestic.apply(2500)).isEqualTo(699L);
        assertThatIllegalArgumentException().isThrownBy(() -> domestic.apply(0))
                .withMessage("weight must be positive: 0 g");
    }

    @Test
    void logFormatFixesLevelThenComponent() {
        Function<String, String> cartWarnings = LogFormat.of(Level.WARN).apply("cart");
        assertThat(cartWarnings.apply("stock low for MUG-0001")).isEqualTo("[WARN ] cart: stock low for MUG-0001");
        assertThat(LogFormat.curried().apply(Level.ERROR).apply("payment").apply("declined"))
                .isEqualTo("[ERROR] payment: declined");
    }

    @Test
    void demoPrintsCurriedAndPartiallyAppliedFunctions() {
        assertThat(Console.capture(() -> CurryingDemo.main(new String[0]))).isEqualTo("""
                -- curry / uncurry / partial / flip
                minus(10, 3)          = 7
                curry(minus)(10)(3)   = 7
                partial(minus, 10)(3) = 7
                flip(minus)(10, 3)    = -7
                -- zone -> weight -> price: fix the zone once, reuse the tariff
                DOMESTIC:  500 g -> 4.99   1500 g -> 5.99   2500 g -> 6.99
                EU:        500 g -> 9.99   1500 g -> 12.49   2500 g -> 14.99
                WORLD:     500 g -> 19.99   1500 g -> 24.99   2500 g -> 29.99
                -- level -> component -> message
                [WARN ] cart: stock low for MUG-0001
                [WARN ] cart: 2 carts expired
                [INFO ] checkout: order A-1 paid
                """);
    }
}
