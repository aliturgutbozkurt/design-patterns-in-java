package io.github.aliturgutbozkurt.patterns.m09.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. A validated, normalised sign-up. */
public record Signup(String username, String email, int age, Country country, Referral referral) {

    public Signup {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(email, "email");
        Objects.requireNonNull(country, "country");
        Objects.requireNonNull(referral, "referral");
    }
}
