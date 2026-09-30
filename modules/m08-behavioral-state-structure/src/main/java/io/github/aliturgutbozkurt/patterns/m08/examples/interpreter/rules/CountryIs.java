package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules;

import java.util.Objects;

/**
 * Terminal expression: {@code country = code}.
 *
 * @see "m08 lesson, section Interpreter — Classic Java"
 */
public record CountryIs(String code) implements Rule {

    public CountryIs {
        Objects.requireNonNull(code, "code");
    }

    @Override
    public boolean interpret(Customer customer) {
        return customer.country().equals(code);
    }

    @Override
    public String render() {
        return "country = " + code;
    }
}
