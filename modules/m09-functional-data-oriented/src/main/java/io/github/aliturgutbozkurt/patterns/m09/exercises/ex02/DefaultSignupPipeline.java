package io.github.aliturgutbozkurt.patterns.m09.exercises.ex02;

import java.util.List;
import java.util.Objects;

/**
 * Assignment 02 — railway-style sign-up validation. Stage 1 collects every field error; stage 2 asks the registry,
 * fail-fast, and only when stage 1 succeeded.
 */
public class DefaultSignupPipeline implements SignupPipeline {

    private final UserRegistry registry;

    public DefaultSignupPipeline(UserRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public Result<Signup, List<SignupError>> validate(RawSignup raw) {
        // TODO(ex02): write one parser per field, each returning Result<X, SignupError>
        //             (username, email, age, country, referral), collect ALL their errors in field order,
        //             and only if there are none ask the registry (isTaken, then isValidReferral) fail-fast.
        //             Use `registry` only in stage 2.
        throw new UnsupportedOperationException("TODO(ex02): implement DefaultSignupPipeline.validate");
    }
}
