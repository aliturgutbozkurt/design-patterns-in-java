package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules;

/**
 * Terminal expression: {@code spent >= cents} (lifetime spending).
 *
 * @see "m08 lesson, section Interpreter — Classic Java"
 */
public record SpentAtLeast(long cents) implements Rule {

    @Override
    public boolean interpret(Customer customer) {
        return customer.spentCents() >= cents;
    }

    @Override
    public String render() {
        return "spent >= " + cents;
    }
}
