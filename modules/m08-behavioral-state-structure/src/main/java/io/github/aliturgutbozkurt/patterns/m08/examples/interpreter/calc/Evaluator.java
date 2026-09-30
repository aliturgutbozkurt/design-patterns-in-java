package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Binary;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Let;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Neg;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Num;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Var;
import java.util.HashMap;
import java.util.Map;

/**
 * Interpreter's {@code interpret}: a recursive exhaustive {@code switch} over the sealed {@link Expr}. The
 * environment maps names to values; {@code let} evaluates its body in an extended copy, so a binding never leaks
 * out of its body. Overflow throws {@link ArithmeticException} ({@code Math.*Exact}) instead of wrapping around.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public final class Evaluator {

    private Evaluator() {}

    public static long evaluate(Expr expr) {
        return evaluate(expr, Map.of());
    }

    public static long evaluate(Expr expr, Map<String, Long> env) {
        return switch (expr) {
            case Num(var value) -> value;
            case Var(var name) -> lookup(env, name);
            case Neg(var operand) -> Math.negateExact(evaluate(operand, env));
            case Binary(var op, var left, var right) -> apply(op, evaluate(left, env), evaluate(right, env));
            case Let(var name, var value, var body) -> evaluate(body, bind(env, name, evaluate(value, env)));
        };
    }

    /** One operator on two values; {@code DIV} truncates toward zero. */
    public static long apply(Op op, long left, long right) {
        return switch (op) {
            case ADD -> Math.addExact(left, right);
            case SUB -> Math.subtractExact(left, right);
            case MUL -> Math.multiplyExact(left, right);
            case DIV -> {
                if (right == 0) {
                    throw new EvalException("division by zero");
                }
                yield Math.divideExact(left, right);
            }
        };
    }

    private static long lookup(Map<String, Long> env, String name) {
        Long value = env.get(name);
        if (value == null) {
            throw new EvalException("unknown variable: " + name);
        }
        return value;
    }

    private static Map<String, Long> bind(Map<String, Long> env, String name, long value) {
        var extended = new HashMap<>(env);
        extended.put(name, value);
        return extended;
    }
}
