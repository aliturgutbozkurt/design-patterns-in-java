package io.github.aliturgutbozkurt.patterns.m08.exercises.ex02;

/**
 * GIVEN — do not modify. Prefix operators. {@code not} binds more loosely than the comparisons ({@code not a = b}
 * means {@code not (a = b)}); unary minus binds tightest of all operators.
 */
public enum UnaryOp {
    NEG("-", 7), NOT("not", 3);

    private final String symbol;
    private final int precedence;

    UnaryOp(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    public String symbol() {
        return symbol;
    }

    /** Higher binds tighter; compare with {@link BinaryOp#precedence()}. */
    public int precedence() {
        return precedence;
    }
}
