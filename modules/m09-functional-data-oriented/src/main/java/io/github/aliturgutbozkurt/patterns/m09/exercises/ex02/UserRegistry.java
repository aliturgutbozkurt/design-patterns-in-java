package io.github.aliturgutbozkurt.patterns.m09.exercises.ex02;

/** GIVEN — do not modify. The external service; the pipeline may call it only for well-formed input. */
public interface UserRegistry {

    boolean isTaken(String username);

    boolean isValidReferral(String code);
}
