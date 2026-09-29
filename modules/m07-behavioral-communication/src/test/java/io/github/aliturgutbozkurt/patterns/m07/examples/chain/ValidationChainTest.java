package io.github.aliturgutbozkurt.patterns.m07.examples.chain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation.Invalid;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation.SignUp;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation.SignUpRules;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation.Valid;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation.Validator;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ValidationChainTest {

    private static final SignUp BAD = new SignUp("ada.example.com", "short", 16);
    private static final SignUp GOOD = new SignUp("ada@example.com", "s3cret-pass", 36);

    @Test
    void collectAllReportsEveryErrorInChainOrder() {
        assertThat(SignUpRules.collectAll().validate(BAD)).isEqualTo(new Invalid(List.of(
                "email must contain @",
                "password must have at least 8 characters",
                "password must contain a digit",
                "age must be at least 18")));
    }

    @Test
    void failFastReportsOnlyTheFirstErrorAndDoesNotInvokeLaterValidators() {
        var laterCalls = new AtomicInteger();
        Validator<SignUp> counting = value -> {
            laterCalls.incrementAndGet();
            return new Valid();
        };
        var result = SignUpRules.emailHasAt().andThen(SignUpRules.adult()).andThen(counting).validate(BAD);
        assertThat(result).isEqualTo(new Invalid(List.of("email must contain @")));
        assertThat(laterCalls).hasValue(0);
    }

    @Test
    void collectAllInvokesEveryValidator() {
        var calls = new AtomicInteger();
        Validator<SignUp> counting = value -> {
            calls.incrementAndGet();
            return new Valid();
        };
        SignUpRules.emailHasAt().and(counting).and(counting).validate(BAD);
        assertThat(calls).hasValue(2);
    }

    @Test
    void allValidInputGivesValid() {
        assertThat(SignUpRules.collectAll().validate(GOOD)).isEqualTo(new Valid());
        assertThat(SignUpRules.failFast().validate(GOOD).isValid()).isTrue();
    }

    @Test
    void invalidErrorsListIsImmutable() {
        var errors = new ArrayList<>(List.of("e1"));
        var invalid = new Invalid(errors);
        errors.add("e2");
        assertThat(invalid.errors()).containsExactly("e1").isUnmodifiable();
        assertThatIllegalArgumentException().isThrownBy(() -> new Invalid(List.of()));
    }

    @Test
    void demoPrintsBothPolicies() {
        assertThat(Console.capture(() -> ValidationChainDemo.main(new String[0]))).isEqualTo("""
                SignUp[email=ada.example.com, password=short, age=16]
                  collect-all: Invalid[errors=[email must contain @, password must have at least 8 characters, \
                password must contain a digit, age must be at least 18]]
                  fail-fast:   Invalid[errors=[email must contain @]]
                SignUp[email=ada@example.com, password=s3cret-pass, age=36]
                  collect-all: Valid[]
                  fail-fast:   Valid[]
                """);
    }
}
