package io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression;

/**
 * Two operations over the expression tree, both recursive exhaustive {@code switch} expressions over the sealed
 * {@link Expr}: one computes a value, one renders text with only the parentheses precedence requires.
 *
 * @see "m05 lesson, section Composite — modern Java 27"
 */
public final class Expressions {

    private Expressions() {}

    /** The value of the tree; overflow throws {@link ArithmeticException} instead of wrapping around. */
    public static long evaluate(Expr expr) {
        return switch (expr) {
            case Num(var value) -> value;
            case Add(var left, var right) -> Math.addExact(evaluate(left), evaluate(right));
            case Mul(var left, var right) -> Math.multiplyExact(evaluate(left), evaluate(right));
            case Neg(var operand) -> Math.negateExact(evaluate(operand));
        };
    }

    /** Infix text, e.g. {@code (1 + 2) * 3}; a negation of anything but a non-negative number gets parentheses. */
    public static String render(Expr expr) {
        return switch (expr) {
            case Num(var value) -> Long.toString(value);
            case Add(var left, var right) -> operand(left, expr) + " + " + operand(right, expr);
            case Mul(var left, var right) -> operand(left, expr) + " * " + operand(right, expr);
            case Neg(Num(var value)) when value >= 0 -> "-" + value;
            case Neg(var operand) -> "-(" + render(operand) + ")";
        };
    }

    private static String operand(Expr child, Expr parent) {
        String text = render(child);
        return precedence(child) < precedence(parent) ? "(" + text + ")" : text;
    }

    private static int precedence(Expr expr) {
        return switch (expr) {
            case Add _ -> 1;
            case Mul _ -> 2;
            case Neg _ -> 3;
            case Num _ -> 4;
        };
    }
}
