package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules;

import java.util.Objects;

/**
 * Terminal expression: {@code tag name}.
 *
 * @see "m08 lesson, section Interpreter — Classic Java"
 */
public record HasTag(String tag) implements Rule {

    public HasTag {
        Objects.requireNonNull(tag, "tag");
    }

    @Override
    public boolean interpret(Customer customer) {
        return customer.tags().contains(tag);
    }

    @Override
    public String render() {
        return "tag " + tag;
    }
}
