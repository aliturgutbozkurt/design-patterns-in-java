package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules;

import java.util.List;

/**
 * Interpreter's abstract expression: a sentence of the promotion-eligibility language. Terminal rules test one fact
 * about a {@link Customer}; non-terminal rules combine other rules. The combinators mirror
 * {@code java.util.function.Predicate.and/or/negate}.
 *
 * @see "m08 lesson, section Interpreter — Classic Java"
 */
public interface Rule {

    /** Binding strength used by {@link #render()} to decide on parentheses. */
    int OR = 1;
    int AND = 2;
    int NOT = 3;
    int ATOM = 4;

    /** Evaluates the sentence against the context. */
    boolean interpret(Customer customer);

    /** The sentence as text, with parentheses only where precedence needs them. */
    String render();

    default int precedence() {
        return ATOM;
    }

    default Rule and(Rule other) {
        return new AllOf(List.of(this, other));
    }

    default Rule or(Rule other) {
        return new AnyOf(List.of(this, other));
    }

    default Rule negate() {
        return new Not(this);
    }

    /** {@code rule.render()}, in parentheses if it binds more loosely than {@code context}. */
    static String renderInside(Rule rule, int context) {
        return rule.precedence() < context ? "(" + rule.render() + ")" : rule.render();
    }
}
