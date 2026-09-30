package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Binary;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Let;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Neg;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Num;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Var;

/**
 * AST → text with only the parentheses the parser needs: a left operand is wrapped if it binds more loosely than
 * its operator, a right operand also if it binds equally (left associativity), and a {@code let} is always wrapped
 * when it is an operand. For every tree the parser can produce, {@code Parser.parse(print(e)).equals(e)}.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public final class Printer {

    private static final int LET = 0;
    private static final int UNARY = 3;
    private static final int ATOM = 4;

    private Printer() {}

    public static String print(Expr expr) {
        return switch (expr) {
            case Num(var value) -> Long.toString(value);
            case Var(var name) -> name;
            case Neg(var operand) -> "-" + wrap(operand, precedence(operand) < UNARY);
            case Binary(var op, var left, var right) -> wrap(left, precedence(left) < op.precedence())
                    + " " + op.symbol() + " " + wrap(right, precedence(right) <= op.precedence());
            case Let(var name, var value, var body) -> "let " + name + " = " + print(value) + " in " + print(body);
        };
    }

    private static String wrap(Expr expr, boolean parenthesize) {
        return parenthesize ? "(" + print(expr) + ")" : print(expr);
    }

    private static int precedence(Expr expr) {
        return switch (expr) {
            case Let _ -> LET;
            case Binary(var op, _, _) -> op.precedence();
            case Neg _ -> UNARY;
            case Num(var value) when value < 0 -> UNARY; // printed with a leading '-'
            case Num _, Var _ -> ATOM;
        };
    }
}
