package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

/**
 * The binary operators, with their symbol and precedence (higher binds tighter). All are left-associative.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public enum Op {
    ADD("+", 1), SUB("-", 1), MUL("*", 2), DIV("/", 2);

    private final String symbol;
    private final int precedence;

    Op(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    public String symbol() {
        return symbol;
    }

    public int precedence() {
        return precedence;
    }
}
