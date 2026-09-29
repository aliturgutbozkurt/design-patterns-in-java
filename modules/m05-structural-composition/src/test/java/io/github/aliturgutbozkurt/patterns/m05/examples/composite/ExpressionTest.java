package io.github.aliturgutbozkurt.patterns.m05.examples.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Add;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Expr;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Expressions;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Mul;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Neg;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Num;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import org.junit.jupiter.api.Test;

class ExpressionTest {

    private static final Expr ONE_PLUS_TWO_TIMES_THREE = new Mul(new Add(new Num(1), new Num(2)), new Num(3));
    private static final Expr ONE_PLUS_TWO_THREES = new Add(new Num(1), new Mul(new Num(2), new Num(3)));

    @Test
    void evaluatesNestedExpressions() {
        assertThat(Expressions.evaluate(ONE_PLUS_TWO_TIMES_THREE)).isEqualTo(9);
        assertThat(Expressions.evaluate(ONE_PLUS_TWO_THREES)).isEqualTo(7);
        assertThat(Expressions.evaluate(new Neg(new Add(new Num(2), new Num(3))))).isEqualTo(-5);
        assertThat(Expressions.evaluate(new Num(42))).isEqualTo(42);
    }

    @Test
    void rendersParenthesesOnlyWherePrecedenceNeedsThem() {
        assertThat(Expressions.render(ONE_PLUS_TWO_TIMES_THREE)).isEqualTo("(1 + 2) * 3");
        assertThat(Expressions.render(ONE_PLUS_TWO_THREES)).isEqualTo("1 + 2 * 3");
        assertThat(Expressions.render(new Add(new Add(new Num(1), new Num(2)), new Num(3)))).isEqualTo("1 + 2 + 3");
        assertThat(Expressions.render(new Mul(new Neg(new Num(2)), new Num(3)))).isEqualTo("-2 * 3");
    }

    @Test
    void negationParenthesisesEverythingButAPlainNumber() {
        assertThat(Expressions.render(new Neg(new Num(4)))).isEqualTo("-4");
        assertThat(Expressions.render(new Neg(new Neg(new Num(4))))).isEqualTo("-(-4)");
        assertThat(Expressions.render(new Neg(new Mul(new Num(2), new Num(3))))).isEqualTo("-(2 * 3)");
        assertThat(Expressions.render(new Neg(new Num(-4)))).isEqualTo("-(-4)");
        assertThat(Expressions.evaluate(new Neg(new Neg(new Num(4))))).isEqualTo(4);
    }

    @Test
    void deepTreeEvaluatesWithoutSpecialCases() {
        Expr sum = new Num(1);
        for (int i = 2; i <= 1000; i++) {
            sum = new Add(sum, new Num(i));
        }
        assertThat(Expressions.evaluate(sum)).isEqualTo(500_500);
        assertThat(Expressions.render(sum)).startsWith("1 + 2 + 3 + ").endsWith(" + 999 + 1000");
    }

    @Test
    void overflowIsAnErrorNotAWrongAnswer() {
        assertThatThrownBy(() -> Expressions.evaluate(new Mul(new Num(Long.MAX_VALUE), new Num(2))))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void nodesRejectNullChildren() {
        assertThatThrownBy(() -> new Add(new Num(1), null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Neg(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void demoPrintsTextAndValueOfEachTree() {
        assertThat(Console.capture(() -> ExpressionDemo.main(new String[0]))).isEqualTo("""
                (1 + 2) * 3 = 9
                1 + 2 * 3 = 7
                -(-4) = 4
                -(2 * 3) + 10 = 4
                1 + 2 + ... + 1000 (a tree 1000 levels deep) = 500500
                """);
    }
}
