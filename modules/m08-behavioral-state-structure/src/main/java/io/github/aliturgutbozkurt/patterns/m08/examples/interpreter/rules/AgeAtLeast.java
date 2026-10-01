package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules;

/**
 * Terminal expression: {@code age >= years}.
 *
 * @see "m08 lesson, section Interpreter — Classic Java"
 */
public record AgeAtLeast(int years) implements Rule {

    @Override
    public boolean interpret(Customer customer) {
        return customer.age() >= years;
    }

    @Override
    public String render() {
        return "age >= " + years;
    }
}
