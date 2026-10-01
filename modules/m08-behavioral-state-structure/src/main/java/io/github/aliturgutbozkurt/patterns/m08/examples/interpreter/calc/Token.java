package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

import java.util.Objects;

/**
 * A token of the calculator language. Punctuation is an {@code enum} that implements the sealed interface, the rest
 * are records, so one {@code switch} can mix {@code case Symbol.LPAREN} with {@code case Number(var value)}.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public sealed interface Token {

    /** The token as it appears in the source. */
    String text();

    enum Symbol implements Token {
        PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), LPAREN("("), RPAREN(")"), EQUALS("=");

        private final String text;

        Symbol(String text) {
            this.text = text;
        }

        @Override
        public String text() {
            return text;
        }
    }

    record Number(long value) implements Token {
        @Override
        public String text() {
            return Long.toString(value);
        }
    }

    record Ident(String name) implements Token {
        public Ident {
            Objects.requireNonNull(name, "name");
        }

        @Override
        public String text() {
            return name;
        }
    }

    /** {@code let} or {@code in}. */
    record Keyword(String word) implements Token {
        public Keyword {
            if (!word.equals("let") && !word.equals("in")) {
                throw new IllegalArgumentException("not a keyword: " + word);
            }
        }

        @Override
        public String text() {
            return word;
        }
    }

    record End() implements Token {
        @Override
        public String text() {
            return "end";
        }
    }
}
