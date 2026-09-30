package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Non-terminal expression: every rule holds. Stops at the first false rule; an empty {@code AllOf} is true.
 *
 * @see "m08 lesson, section Interpreter — Classic Java"
 */
public record AllOf(List<Rule> rules) implements Rule {

    public AllOf {
        rules = List.copyOf(rules);
    }

    @Override
    public boolean interpret(Customer customer) {
        return rules.stream().allMatch(rule -> rule.interpret(customer));
    }

    @Override
    public String render() {
        return rules.isEmpty() ? "true"
                : rules.stream().map(rule -> Rule.renderInside(rule, AND)).collect(Collectors.joining(" and "));
    }

    @Override
    public int precedence() {
        return rules.isEmpty() ? ATOM : AND;
    }
}
