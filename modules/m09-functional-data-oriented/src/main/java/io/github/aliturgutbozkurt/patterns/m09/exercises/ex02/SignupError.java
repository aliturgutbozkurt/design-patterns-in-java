package io.github.aliturgutbozkurt.patterns.m09.exercises.ex02;

/** GIVEN — do not modify. Every reason a sign-up can be refused, as data. */
public sealed interface SignupError permits SignupError.Missing, SignupError.TooShort, SignupError.InvalidFormat,
        SignupError.OutOfRange, SignupError.UnknownCountry, SignupError.UsernameTaken, SignupError.InvalidReferral {

    /** The field was {@code null} or blank. */
    record Missing(String field) implements SignupError {}

    record TooShort(String field, int minLength) implements SignupError {}

    record InvalidFormat(String field) implements SignupError {}

    record OutOfRange(String field, int min, int max) implements SignupError {}

    record UnknownCountry(String value) implements SignupError {}

    record UsernameTaken(String username) implements SignupError {}

    record InvalidReferral(String code) implements SignupError {}
}
