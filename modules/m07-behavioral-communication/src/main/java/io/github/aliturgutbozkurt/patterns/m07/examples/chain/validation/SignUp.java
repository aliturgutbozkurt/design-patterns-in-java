package io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation;

import java.util.Objects;

/**
 * Raw sign-up input to be validated.
 *
 * @see "m07 lesson, section Chain of Responsibility — validation chains"
 */
public record SignUp(String email, String password, int age) {

    public SignUp {
        Objects.requireNonNull(email, "email");
        Objects.requireNonNull(password, "password");
    }
}
