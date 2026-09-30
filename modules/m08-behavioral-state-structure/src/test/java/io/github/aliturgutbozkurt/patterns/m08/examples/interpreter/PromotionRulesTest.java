package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.AgeAtLeast;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.AllOf;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.AnyOf;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.CountryIs;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.Customer;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.HasTag;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.Not;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.Promotion;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.Rule;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.SpentAtLeast;
import io.github.aliturgutbozkurt.patterns.m08.support.Console;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PromotionRulesTest {

    private static final List<Customer> CUSTOMERS = PromotionRulesDemo.customers();
    private static final List<Promotion> PROMOTIONS = PromotionRulesDemo.promotions();
    private static final List<Rule> RULES = List.of(
            new AgeAtLeast(18), new CountryIs("TR"), new SpentAtLeast(100_000), new HasTag("vip"),
            new AgeAtLeast(18).and(new CountryIs("TR").or(new HasTag("vip"))),
            new HasTag("employee").negate(), new AllOf(List.of()), new AnyOf(List.of()));

    /** A test-only terminal rule: the hierarchy is open, and this one counts how often it is asked. */
    private static final class Counting implements Rule {
        private final boolean answer;
        private int calls;

        Counting(boolean answer) {
            this.answer = answer;
        }

        @Override
        public boolean interpret(Customer customer) {
            calls++;
            return answer;
        }

        @Override
        public String render() {
            return "counting";
        }
    }

    @Test
    void eligibilityForEveryCustomerAndPromotion() {
        Map<String, List<Boolean>> expected = Map.of(
                "WELCOME", List.of(true, false, true, false),
                "BIGSPENDER", List.of(false, true, true, false),
                "YOUTH", List.of(false, true, false, false),
                "NEWCOMER", List.of(true, false, false, false));
        for (Promotion promotion : PROMOTIONS) {
            assertThat(CUSTOMERS.stream().map(promotion::isEligible).toList())
                    .as(promotion.code())
                    .isEqualTo(expected.get(promotion.code()));
        }
    }

    @Test
    void allOfAndAnyOfShortCircuit() {
        var customer = CUSTOMERS.getFirst();
        var afterFalse = new Counting(true);
        assertThat(new AllOf(List.of(new AgeAtLeast(99), afterFalse)).interpret(customer)).isFalse();
        var afterTrue = new Counting(false);
        assertThat(new AnyOf(List.of(new AgeAtLeast(1), afterTrue)).interpret(customer)).isTrue();
        assertThat(afterFalse.calls).isZero();
        assertThat(afterTrue.calls).isZero();
        var evaluated = new Counting(true);
        assertThat(new AllOf(List.of(new AgeAtLeast(1), evaluated)).interpret(customer)).isTrue();
        assertThat(evaluated.calls).isEqualTo(1);
    }

    @Test
    void doubleNegationIsEquivalentToTheRule() {
        for (Rule rule : RULES) {
            for (Customer customer : CUSTOMERS) {
                assertThat(new Not(new Not(rule)).interpret(customer)).isEqualTo(rule.interpret(customer));
            }
        }
    }

    @Test
    void deMorganHoldsOverTheTable() {
        for (Rule a : RULES) {
            for (Rule b : RULES) {
                Rule left = a.and(b).negate();
                Rule right = a.negate().or(b.negate());
                for (Customer customer : CUSTOMERS) {
                    assertThat(left.interpret(customer)).isEqualTo(right.interpret(customer));
                }
            }
        }
    }

    @Test
    void renderUsesParenthesesOnlyWhereNeeded() {
        assertThat(new AgeAtLeast(18).and(new CountryIs("TR").or(new HasTag("vip"))).render())
                .isEqualTo("age >= 18 and (country = TR or tag vip)");
        assertThat(new AgeAtLeast(18).and(new CountryIs("TR")).or(new HasTag("vip")).render())
                .isEqualTo("age >= 18 and country = TR or tag vip");
        assertThat(new HasTag("vip").or(new SpentAtLeast(100_000)).negate().render())
                .isEqualTo("not (tag vip or spent >= 100000)");
        assertThat(new HasTag("vip").negate().negate().render()).isEqualTo("not not tag vip");
        assertThat(new AllOf(List.of(new HasTag("a"), new AllOf(List.of(new HasTag("b"), new HasTag("c")))))
                .render()).isEqualTo("tag a and tag b and tag c");
    }

    @Test
    void emptyAllOfIsTrueAndEmptyAnyOfIsFalse() {
        var customer = CUSTOMERS.getFirst();
        assertThat(new AllOf(List.of()).interpret(customer)).isTrue();
        assertThat(new AnyOf(List.of()).interpret(customer)).isFalse();
        assertThat(new AllOf(List.of()).render()).isEqualTo("true");
        assertThat(new AnyOf(List.of()).render()).isEqualTo("false");
    }

    @Test
    void customerCopiesItsTags() {
        var tags = new HashSet<>(Set.of("vip"));
        var customer = new Customer("C-9", 30, "TR", 0, tags);
        tags.add("employee");
        assertThat(customer.tags()).containsExactly("vip");
    }

    @Test
    void demoPrintsTheRulesAndTheEligibilityTable() {
        assertThat(Console.capture(() -> PromotionRulesDemo.main(new String[0]))).isEqualTo("""
                WELCOME     age >= 18 and (country = TR or tag vip)
                BIGSPENDER  spent >= 100000 and not tag employee
                YOUTH       not age >= 18
                NEWCOMER    not (tag vip or spent >= 100000)

                customer    WELCOME     BIGSPENDER  YOUTH       NEWCOMER
                C-1         yes         -           -           yes
                C-2         -           yes         yes         -
                C-3         yes         yes         -           -
                C-4         -           -           -           -
                """);
    }
}
