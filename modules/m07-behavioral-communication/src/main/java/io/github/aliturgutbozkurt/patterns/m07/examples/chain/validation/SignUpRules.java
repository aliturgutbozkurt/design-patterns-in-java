package io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation;

/**
 * The sign-up rules as {@link Validator}s, and the two chains built from them.
 *
 * @see "m07 lesson, section Chain of Responsibility — validation chains"
 */
public final class SignUpRules {

    private SignUpRules() {}

    public static Validator<SignUp> emailHasAt() {
        return Validator.rule(s -> s.email().contains("@"), "email must contain @");
    }

    public static Validator<SignUp> passwordLongEnough() {
        return Validator.rule(s -> s.password().length() >= 8, "password must have at least 8 characters");
    }

    public static Validator<SignUp> passwordHasDigit() {
        return Validator.rule(s -> s.password().chars().anyMatch(Character::isDigit), "password must contain a digit");
    }

    public static Validator<SignUp> adult() {
        return Validator.rule(s -> s.age() >= 18, "age must be at least 18");
    }

    /** Every rule runs; the result lists every error. */
    public static Validator<SignUp> collectAll() {
        return emailHasAt().and(passwordLongEnough()).and(passwordHasDigit()).and(adult());
    }

    /** The first failing rule stops the chain. */
    public static Validator<SignUp> failFast() {
        return emailHasAt().andThen(passwordLongEnough()).andThen(passwordHasDigit()).andThen(adult());
    }
}
