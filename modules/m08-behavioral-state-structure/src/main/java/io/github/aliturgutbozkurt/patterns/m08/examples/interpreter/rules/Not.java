package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules;

import java.util.Objects;

/**
 * Non-terminal expression: the rule does not hold.
 *
 * @see "m08 lesson, section Interpreter — Classic Java"
 */
public record Not(Rule rule) implements Rule {

    public Not {
        Objects.requireNonNull(rule, "rule");
    }

    @Override
    public boolean interpret(Customer customer) {
        return !rule.interpret(customer);
    }

    @Override
    public String render() {
        return "not " + Rule.renderInside(rule, NOT);
    }

    @Override
    public int precedence() {
        return NOT;
    }
}
