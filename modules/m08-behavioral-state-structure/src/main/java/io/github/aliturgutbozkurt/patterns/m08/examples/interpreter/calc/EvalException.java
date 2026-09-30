package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

/**
 * A well-formed program that cannot be evaluated: an unknown variable or a division by zero.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public final class EvalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EvalException(String message) {
        super(message);
    }
}
