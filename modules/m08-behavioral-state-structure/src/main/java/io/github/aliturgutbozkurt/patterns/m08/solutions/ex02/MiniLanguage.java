package io.github.aliturgutbozkurt.patterns.m08.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.BinaryOp;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.EvalException;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Binary;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Bool;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.If;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Num;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Unary;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Var;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Language;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.UnaryOp;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Value;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Value.BoolValue;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Value.NumValue;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Reference solution for assignment 02: three recursive exhaustive {@code switch} functions over the sealed
 * {@link Expr}, with run-time type checks, short-circuit {@code and}/{@code or} and a round-trip printer.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public final class MiniLanguage implements Language {

    private static final int IF = 0;
    private static final int ATOM = 8;

    /** A pair of operand values, so {@code EQ} can match both types at once. */
    private record Operands(Value left, Value right) {}

    @Override
    public Value evaluate(Expr expr, Map<String, Value> env) {
        Objects.requireNonNull(expr, "expr");
        Objects.requireNonNull(env, "env");
        return switch (expr) {
            case Num(var value) -> new NumValue(value);
            case Bool(var value) -> new BoolValue(value);
            case Var(var name) -> {
                Value value = env.get(name);
                if (value == null) {
                    throw new EvalException("unknown variable: " + name);
                }
                yield value;
            }
            case Unary(var op, var operand) -> switch (op) {
                case NEG -> new NumValue(Math.negateExact(number(op.name(), evaluate(operand, env))));
                case NOT -> new BoolValue(!bool(op.name(), evaluate(operand, env)));
            };
            case Binary(var op, var left, var right) -> binary(op, left, right, env);
            case If(var condition, var then, var otherwise) ->
                    bool("IF", evaluate(condition, env)) ? evaluate(then, env) : evaluate(otherwise, env);
        };
    }

    private Value binary(BinaryOp op, Expr left, Expr right, Map<String, Value> env) {
        String name = op.name();
        return switch (op) {
            case AND -> new BoolValue(bool(name, evaluate(left, env)) && bool(name, evaluate(right, env)));
            case OR -> new BoolValue(bool(name, evaluate(left, env)) || bool(name, evaluate(right, env)));
            case EQ -> new BoolValue(equal(evaluate(left, env), evaluate(right, env)));
            case LT -> new BoolValue(number(name, evaluate(left, env)) < number(name, evaluate(right, env)));
            case LE -> new BoolValue(number(name, evaluate(left, env)) <= number(name, evaluate(right, env)));
            case ADD -> new NumValue(Math.addExact(number(name, evaluate(left, env)), number(name, evaluate(right, env))));
            case SUB -> new NumValue(
                    Math.subtractExact(number(name, evaluate(left, env)), number(name, evaluate(right, env))));
            case MUL -> new NumValue(
                    Math.multiplyExact(number(name, evaluate(left, env)), number(name, evaluate(right, env))));
            case DIV -> {
                long dividend = number(name, evaluate(left, env));
                long divisor = number(name, evaluate(right, env));
                if (divisor == 0) {
                    throw new EvalException("division by zero");
                }
                yield new NumValue(Math.divideExact(dividend, divisor));
            }
        };
    }

    private static boolean equal(Value left, Value right) {
        return switch (new Operands(left, right)) {
            case Operands(NumValue(var a), NumValue(var b)) -> a == b;
            case Operands(BoolValue(var a), BoolValue(var b)) -> a == b;
            case Operands(NumValue _, BoolValue _), Operands(BoolValue _, NumValue _) ->
                    throw typeError("EQ", typeOf(left), right);
        };
    }

    private static long number(String op, Value value) {
        return switch (value) {
            case NumValue(var n) -> n;
            case BoolValue _ -> throw typeError(op, "NUM", value);
        };
    }

    private static boolean bool(String op, Value value) {
        return switch (value) {
            case BoolValue(var b) -> b;
            case NumValue _ -> throw typeError(op, "BOOL", value);
        };
    }

    private static EvalException typeError(String op, String expected, Value actual) {
        return new EvalException("type error: " + op + " expects " + expected + " but got " + typeOf(actual));
    }

    private static String typeOf(Value value) {
        return switch (value) {
            case NumValue _ -> "NUM";
            case BoolValue _ -> "BOOL";
        };
    }

    @Override
    public String print(Expr expr) {
        Objects.requireNonNull(expr, "expr");
        return switch (expr) {
            case Num(var value) -> Long.toString(value);
            case Bool(var value) -> Boolean.toString(value);
            case Var(var name) -> name;
            case Unary(var op, var operand) -> switch (op) {
                case NEG -> "-" + wrap(operand, precedence(operand) < op.precedence());
                case NOT -> "not " + wrap(operand, precedence(operand) < op.precedence());
            };
            case Binary(var op, var left, var right) -> {
                // comparisons do not chain, so a comparison on the left needs parentheses too
                boolean leftNeeds = op.isComparison() ? precedence(left) <= op.precedence()
                        : precedence(left) < op.precedence();
                yield wrap(left, leftNeeds) + " " + op.symbol() + " " + wrap(right,
                        precedence(right) <= op.precedence());
            }
            case If(var condition, var then, var otherwise) ->
                    "if " + print(condition) + " then " + print(then) + " else " + print(otherwise);
        };
    }

    private String wrap(Expr expr, boolean parenthesize) {
        return parenthesize ? "(" + print(expr) + ")" : print(expr);
    }

    private static int precedence(Expr expr) {
        return switch (expr) {
            case If _ -> IF;
            case Binary(var op, _, _) -> op.precedence();
            case Unary(var op, _) -> op.precedence();
            case Num(var value) when value < 0 -> UnaryOp.NEG.precedence(); // printed with a leading '-'
            case Num _, Bool _, Var _ -> ATOM;
        };
    }

    @Override
    public Set<String> freeVariables(Expr expr) {
        Objects.requireNonNull(expr, "expr");
        var names = new TreeSet<String>();
        collect(expr, names);
        return Collections.unmodifiableSet(names);
    }

    private static void collect(Expr expr, Set<String> names) {
        switch (expr) {
            case Var(var name) -> names.add(name);
            case Num _, Bool _ -> { }
            case Unary(_, var operand) -> collect(operand, names);
            case Binary(_, var left, var right) -> {
                collect(left, names);
                collect(right, names);
            }
            case If(var condition, var then, var otherwise) -> {
                collect(condition, names);
                collect(then, names);
                collect(otherwise, names);
            }
        }
    }
}
