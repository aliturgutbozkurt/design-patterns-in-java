package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.checkout;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.List;
import org.junit.jupiter.api.Test;

class CheckoutRulesTest {

    private static final Address HOME = new Address("Alice", "Street 1", "Istanbul", "34710");
    private static final Address NONE = new Address("", "", "", "");

    private static CandidateLine physical(String sku, int quantity, int stock) {
        return new CandidateLine(new Sku(sku), ProductType.PHYSICAL, quantity, stock);
    }

    private static CandidateLine digital(String sku, int quantity) {
        return new CandidateLine(new Sku(sku), ProductType.DIGITAL, quantity, 0);
    }

    @Test
    void emptyCartStopsTheChainAtOnce() {
        assertThat(CheckoutRules.standard().check(new CheckoutCandidate(List.of(), NONE, "X", true, "")))
                .containsExactly("empty cart");
    }

    @Test
    void everyOtherRuleRunsAndReportsInChainOrder() {
        var candidate = new CheckoutCandidate(List.of(physical("ELE-001", 11, 10), physical("TOY-001", 7, 6)), NONE,
                "AUTUMN5", true, " ");

        assertThat(CheckoutRules.standard().check(candidate)).containsExactly(
                "missing address",
                "quantity limit exceeded: ELE-001",
                "insufficient stock: ELE-001",
                "insufficient stock: TOY-001",
                "expired coupon: AUTUMN5",
                "missing card token");
    }

    @Test
    void digitalLinesNeedNoAddressAndNoStock() {
        var candidate = new CheckoutCandidate(List.of(digital("DIG-001", 10)), NONE, "", false, "tok");

        assertThat(CheckoutRules.standard().check(candidate)).isEmpty();
        assertThat(CheckoutRules.quantityLimit().check(
                new CheckoutCandidate(List.of(digital("DIG-001", 11)), HOME, "", false, "tok")))
                .containsExactly("quantity limit exceeded: DIG-001");
    }

    @Test
    void andCollectsWhileAndThenStopsAtTheFirstFailure() {
        CheckoutRule always = _ -> List.of("a");
        CheckoutRule also = _ -> List.of("b");
        var candidate = new CheckoutCandidate(List.of(), HOME, "", false, "tok");

        assertThat(always.and(also).check(candidate)).containsExactly("a", "b");
        assertThat(always.andThen(also).check(candidate)).containsExactly("a");
    }
}
