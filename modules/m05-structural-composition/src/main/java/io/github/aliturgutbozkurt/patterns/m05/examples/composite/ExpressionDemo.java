package io.github.aliturgutbozkurt.patterns.m05.examples.composite;

import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Add;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Expr;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Expressions;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Mul;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Neg;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression.Num;

/**
 * Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/composite/ExpressionDemo.java}
 *
 * @see "m05 lesson, section Composite"
 */
public final class ExpressionDemo {

    private ExpressionDemo() {}

    public static void main(String[] args) {
        show(new Mul(new Add(new Num(1), new Num(2)), new Num(3)));
        show(new Add(new Num(1), new Mul(new Num(2), new Num(3))));
        show(new Neg(new Neg(new Num(4))));
        show(new Add(new Neg(new Mul(new Num(2), new Num(3))), new Num(10)));

        Expr sum = new Num(1);
        for (int i = 2; i <= 1000; i++) {
            sum = new Add(sum, new Num(i));
        }
        System.out.println("1 + 2 + ... + 1000 (a tree 1000 levels deep) = " + Expressions.evaluate(sum));
    }

    private static void show(Expr expr) {
        System.out.println(Expressions.render(expr) + " = " + Expressions.evaluate(expr));
    }
}
