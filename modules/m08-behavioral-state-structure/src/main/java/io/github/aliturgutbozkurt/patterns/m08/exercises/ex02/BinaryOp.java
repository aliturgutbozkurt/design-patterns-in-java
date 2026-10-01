package io.github.aliturgutbozkurt.patterns.m08.exercises.ex02;

/**
 * GIVEN — do not modify. Infix operators, lowest precedence first. All are left-associative except the comparisons
 * ({@code EQ}, {@code LT}, {@code LE}), which do not chain: {@code a < b < c} is a parse error.
 */
public enum BinaryOp {
    OR("or", 1), AND("and", 2), EQ("=", 4), LT("<", 4), LE("<=", 4), ADD("+", 5), SUB("-", 5), MUL("*", 6),
    DIV("/", 6);

    private final String symbol;
    private final int precedence;

    BinaryOp(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    public String symbol() {
        return symbol;
    }

    /** Higher binds tighter; compare with {@link UnaryOp#precedence()}. */
    public int precedence() {
        return precedence;
    }

    public boolean isComparison() {
        return precedence == 4;
    }
}
