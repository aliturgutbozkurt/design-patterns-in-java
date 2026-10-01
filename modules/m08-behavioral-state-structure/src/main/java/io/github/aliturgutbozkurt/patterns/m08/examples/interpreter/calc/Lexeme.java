package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

import java.util.Objects;

/**
 * A token at its 1-based column. The column lives here rather than in {@link Token}, because an {@code enum}
 * constant such as {@code Symbol.PLUS} is shared by every occurrence.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public record Lexeme(Token token, int column) {

    public Lexeme {
        Objects.requireNonNull(token, "token");
    }

    /** E.g. {@code +@5}. */
    @Override
    public String toString() {
        return token.text() + "@" + column;
    }
}
