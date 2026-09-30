package io.github.aliturgutbozkurt.patterns.m09.exercises.ex02;

import java.util.List;

/** GIVEN — do not modify. Validates a raw form; never throws for bad input. */
public interface SignupPipeline {

    Result<Signup, List<SignupError>> validate(RawSignup raw);
}
