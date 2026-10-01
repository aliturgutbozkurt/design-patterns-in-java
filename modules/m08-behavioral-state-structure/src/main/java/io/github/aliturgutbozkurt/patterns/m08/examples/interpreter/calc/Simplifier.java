package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Binary;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Let;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Neg;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Num;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Var;

/**
 * A new operation added without touching the AST: bottom-up rewriting with nested record patterns and guards.
 * Folds constants (except a division by zero, which is left for the evaluator to report) and applies
 * {@code x + 0}, {@code x - 0}, {@code x * 1}, {@code x / 1}, {@code x * 0} and {@code --x}. Like most simplifiers,
 * {@code x * 0 → 0} drops {@code x}, so an error inside {@code x} (an unknown variable) disappears.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public final class Simplifier {

    private Simplifier() {}

    public static Expr simplify(Expr expr) {
        return switch (expr) {
            case Num _, Var _ -> expr;
            case Neg(var operand) -> negate(simplify(operand));
            case Binary(var op, var left, var right) -> binary(new Binary(op, simplify(left), simplify(right)));
            case Let(var name, var value, var body) -> new Let(name, simplify(value), simplify(body));
        };
    }

    private static Expr negate(Expr operand) {
        return switch (operand) {
            case Num(var value) -> new Num(Math.negateExact(value));
            case Neg(var inner) -> inner;
            case Var _, Binary _, Let _ -> new Neg(operand);
        };
    }

    private static Expr binary(Binary binary) {
        return switch (binary) {
            case Binary(var op, Num(var a), Num(var b)) when op != Op.DIV || b != 0 ->
                    new Num(Evaluator.apply(op, a, b));
            case Binary(var op, var x, Num(var b)) when b == 0 && (op == Op.ADD || op == Op.SUB) -> x;
            case Binary(var op, Num(var a), var x) when a == 0 && op == Op.ADD -> x;
            case Binary(var op, var x, Num(var b)) when b == 1 && (op == Op.MUL || op == Op.DIV) -> x;
            case Binary(var op, Num(var a), var x) when a == 1 && op == Op.MUL -> x;
            case Binary(var op, _, Num(var b)) when b == 0 && op == Op.MUL -> new Num(0);
            case Binary(var op, Num(var a), _) when a == 0 && op == Op.MUL -> new Num(0);
            case Binary unchanged -> unchanged;
        };
    }
}
