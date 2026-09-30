package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules;

import java.util.Objects;

/**
 * A PatternShop promotion: a code plus the rule that decides who may use it.
 *
 * @see "m08 lesson, section Interpreter — Classic Java"
 */
public record Promotion(String code, Rule eligibility) {

    public Promotion {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(eligibility, "eligibility");
    }

    public boolean isEligible(Customer customer) {
        return eligibility.interpret(customer);
    }
}
