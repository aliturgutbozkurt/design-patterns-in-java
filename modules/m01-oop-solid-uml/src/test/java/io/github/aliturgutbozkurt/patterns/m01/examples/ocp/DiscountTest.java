package io.github.aliturgutbozkurt.patterns.m01.examples.ocp;

import static io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after.DiscountRules.fixedOff;
import static io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after.DiscountRules.minimumSpend;
import static io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after.DiscountRules.percentOff;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after.Checkout;
import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after.DiscountRule;
import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.before.PriceCalculator;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DiscountTest {

    static BigDecimal amount(String value) {
        return new BigDecimal(value);
    }

    @ParameterizedTest
    @CsvSource({"REGULAR, 200.00", "STUDENT, 180.00", "VIP, 170.00"})
    void beforePricesEachKnownCustomerType(String type, String expected) {
        assertThat(new PriceCalculator().price(type, amount("200"))).isEqualTo(amount(expected));
    }

    @Test
    void beforeRejectsUnknownCustomerType() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PriceCalculator().price("GOLD", amount("10")))
                .withMessage("unknown customer type: GOLD");
    }

    @ParameterizedTest
    @CsvSource({"REGULAR, 0", "STUDENT, 10", "VIP, 15"})
    void afterMatchesBeforeForTheExistingTypes(String type, int percent) {
        var checkout = percent == 0 ? new Checkout(List.of()) : new Checkout(List.of(percentOff(percent)));
        for (String value : List.of("0", "19.99", "200", "1234.56")) {
            assertThat(checkout.price(amount(value))).isEqualTo(new PriceCalculator().price(type, amount(value)));
        }
    }

    @Test
    void appliesRulesInOrder() {
        assertThat(new Checkout(List.of(percentOff(10), fixedOff(amount("20")))).price(amount("100")))
                .isEqualTo(amount("70.00"));
        assertThat(new Checkout(List.of(fixedOff(amount("20")), percentOff(10))).price(amount("100")))
                .isEqualTo(amount("72.00"));
    }

    @Test
    void priceNeverGoesBelowZero() {
        assertThat(new Checkout(List.of(fixedOff(amount("50")))).price(amount("30"))).isEqualTo(amount("0.00"));
    }

    @Test
    void minimumSpendAppliesTheRuleOnlyFromTheThreshold() {
        var checkout = new Checkout(List.of(minimumSpend(amount("200"), fixedOff(amount("30")))));
        assertThat(checkout.price(amount("199.99"))).isEqualTo(amount("199.99"));
        assertThat(checkout.price(amount("200"))).isEqualTo(amount("170.00"));
    }

    @Test
    void aRuleWrittenOnlyInThisTestWorksWithoutChangingCheckout() {
        DiscountRule halfPrice = price -> price.divide(BigDecimal.TWO);
        assertThat(new Checkout(List.of(halfPrice)).price(amount("9.99"))).isEqualTo(amount("5.00"));
    }

    @Test
    void rejectsInvalidRulesAndAmounts() {
        assertThatIllegalArgumentException().isThrownBy(() -> percentOff(101));
        assertThatIllegalArgumentException().isThrownBy(() -> percentOff(-1));
        assertThatIllegalArgumentException().isThrownBy(() -> fixedOff(amount("-1")));
        assertThatIllegalArgumentException().isThrownBy(() -> new Checkout(List.of()).price(amount("-0.01")));
    }

    @Test
    void demoPrintsBothVersions() {
        assertThat(Console.capture(() -> OcpDemo.main(new String[0]))).isEqualTo("""
                == before: if/else on a customer-type string ==
                REGULAR pays 250.00
                STUDENT pays 225.00
                VIP pays 212.50
                == after: discount rules are values ==
                REGULAR pays 250.00
                STUDENT pays 225.00
                VIP pays 212.50
                VIP_BLACK_FRIDAY pays 212.00 (new lambda rule, Checkout unchanged)
                """);
    }
}
