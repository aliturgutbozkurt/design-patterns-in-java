package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Non-terminal expression: at least one rule holds. Stops at the first true rule; an empty {@code AnyOf} is false.
 *
 * @see "m08 lesson, section Interpreter — Classic Java"
 */
public record AnyOf(List<Rule> rules) implements Rule {

    public AnyOf {
        rules = List.copyOf(rules);
    }

    @Override
    public boolean interpret(Customer customer) {
        return rules.stream().anyMatch(rule -> rule.interpret(customer));
    }

    @Override
    public String render() {
        return rules.isEmpty() ? "false"
                : rules.stream().map(rule -> Rule.renderInside(rule, OR)).collect(Collectors.joining(" or "));
    }

    @Override
    public int precedence() {
        return rules.isEmpty() ? ATOM : OR;
    }
}
