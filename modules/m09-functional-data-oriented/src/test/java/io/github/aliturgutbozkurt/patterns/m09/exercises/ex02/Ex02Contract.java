package io.github.aliturgutbozkurt.patterns.m09.exercises.ex02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Referral.NoReferral;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Referral.ReferredBy;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Result.Err;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Result.Ok;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.InvalidFormat;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.InvalidReferral;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.Missing;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.OutOfRange;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.TooShort;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.UnknownCountry;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.UsernameTaken;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Assignment 02 — railway-style sign-up validation. Each test is one acceptance criterion of the brief. */
public abstract class Ex02Contract {

    protected abstract SignupPipeline newPipeline(UserRegistry registry);

    /** A fake registry: {@code taken} usernames and {@code SPRING26} as the only valid code; records every call. */
    static final class CountingRegistry implements UserRegistry {
        final List<String> calls = new ArrayList<>();
        private final Set<String> taken;

        CountingRegistry(Set<String> taken) {
            this.taken = taken;
        }

        @Override
        public boolean isTaken(String username) {
            calls.add("isTaken " + username);
            return taken.contains(username);
        }

        @Override
        public boolean isValidReferral(String code) {
            calls.add("isValidReferral " + code);
            return code.equals("SPRING26");
        }
    }

    private final CountingRegistry registry = new CountingRegistry(Set.of("grace"));

    private Result<Signup, List<SignupError>> validate(String username, String email, String age, String country,
                                                       String referral) {
        return newPipeline(registry).validate(new RawSignup(username, email, age, country, referral));
    }

    private List<SignupError> errorsOf(Result<Signup, List<SignupError>> result) {
        assertThat(result).isInstanceOf(Err.class);
        return result.fold(_ -> List.of(), errors -> errors);
    }

    @Test
    void validSignupProducesNormalisedValue() {
        assertThat(validate("  Ada_L ", " Ada@Example.COM ", " 36 ", "tr", " spring26 ")).isEqualTo(new Ok<>(
                new Signup("ada_l", "ada@example.com", 36, Country.TR, new ReferredBy("SPRING26"))));
        assertThat(registry.calls).containsExactly("isTaken ada_l", "isValidReferral SPRING26");
    }

    @Test
    void missingFieldsAreReported() {
        assertThat(errorsOf(validate(null, " ", "", null, null))).containsExactly(
                new Missing("username"), new Missing("email"), new Missing("age"), new Missing("country"));
    }

    @Test
    void allFieldErrorsAreCollectedInFieldOrder() {
        assertThat(errorsOf(validate("ab", "not-an-email", "twelve", "XX", ""))).containsExactly(
                new TooShort("username", 3), new InvalidFormat("email"), new InvalidFormat("age"),
                new UnknownCountry("XX"));
    }

    @Test
    void usernameTooShortAndInvalidCharacters() {
        assertThat(errorsOf(validate("ab", "a@b.io", "20", "DE", null))).containsExactly(new TooShort("username", 3));
        assertThat(errorsOf(validate("ada lovelace", "a@b.io", "20", "DE", null)))
                .containsExactly(new InvalidFormat("username"));
        assertThat(errorsOf(validate("a".repeat(21), "a@b.io", "20", "DE", null)))
                .containsExactly(new InvalidFormat("username"));
        assertThat(validate("a".repeat(20), "a@b.io", "20", "DE", null)).isInstanceOf(Ok.class);
    }

    @Test
    void emailIsTrimmedAndLowerCased() {
        String email = validate("ada", "  ADA@Example.Org\t", "20", "DE", null).fold(Signup::email, Object::toString);
        assertThat(email).isEqualTo("ada@example.org");
        assertThat(errorsOf(validate("ada", "ada@example", "20", "DE", null)))
                .containsExactly(new InvalidFormat("email"));
    }

    @Test
    void ageMustBeANumberInRange() {
        assertThat(errorsOf(validate("ada", "a@b.io", "12.5", "DE", null))).containsExactly(new InvalidFormat("age"));
        assertThat(errorsOf(validate("ada", "a@b.io", "0x20", "DE", null))).containsExactly(new InvalidFormat("age"));
        assertThat(errorsOf(validate("ada", "a@b.io", "200", "DE", null)))
                .containsExactly(new OutOfRange("age", 13, 120));
    }

    @ParameterizedTest
    @CsvSource({"12, false", "13, true", "120, true", "121, false"})
    void ageBoundariesAreInclusive(String age, boolean accepted) {
        assertThat(validate("ada", "a@b.io", age, "DE", null) instanceof Ok).isEqualTo(accepted);
    }

    @Test
    void countryIsCaseInsensitive() {
        for (String text : List.of("nl", "NL", " Nl ")) {
            Country country = validate("ada", "a@b.io", "20", text, null).fold(Signup::country, _ -> null);
            assertThat(country).isEqualTo(Country.NL);
        }
    }

    @Test
    void unknownCountryIsReported() {
        assertThat(errorsOf(validate("ada", "a@b.io", "20", " France ", null)))
                .containsExactly(new UnknownCountry("France"));
    }

    @Test
    void blankReferralMeansNoReferral() {
        for (String code : new String[] {null, "", "   "}) {
            Referral referral = validate("ada", "a@b.io", "20", "US", code).fold(Signup::referral, _ -> null);
            assertThat(referral).isEqualTo(new NoReferral());
        }
        assertThat(registry.calls).containsOnly("isTaken ada");
    }

    @Test
    void registryNotCalledWhenFieldsInvalid() {
        validate("grace", "a@b.io", "7", "TR", "WRONG");
        assertThat(registry.calls).isEmpty();
    }

    @Test
    void takenUsernameFailsFast() {
        assertThat(validate(" GRACE ", "g@b.io", "30", "TR", "WRONG"))
                .isEqualTo(new Err<>(List.of(new UsernameTaken("grace"))));
        assertThat(registry.calls).containsExactly("isTaken grace");
    }

    @Test
    void invalidReferralIsReported() {
        assertThat(validate("ada", "a@b.io", "20", "TR", "wrong"))
                .isEqualTo(new Err<>(List.of(new InvalidReferral("WRONG"))));
    }

    @Test
    void errorListIsUnmodifiable() {
        List<SignupError> fieldErrors = errorsOf(validate(null, null, null, null, null));
        assertThatThrownBy(() -> fieldErrors.add(new Missing("x"))).isInstanceOf(UnsupportedOperationException.class);
        List<SignupError> registryErrors = errorsOf(validate("grace", "g@b.io", "30", "TR", null));
        assertThatThrownBy(registryErrors::clear).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void neverThrowsForBadInput() {
        var pipeline = newPipeline(registry);
        for (RawSignup raw : List.of(new RawSignup(null, null, null, null, null),
                new RawSignup("\u0000", "@@", "99999999999999", "  ", "\t"),
                new RawSignup("İstanbul", "a@b@c.d", "-1", "tr_TR", "x y"))) {
            assertThatNoException().isThrownBy(() -> pipeline.validate(raw));
            assertThat(pipeline.validate(raw)).isInstanceOf(Err.class);
        }
    }

    @Test
    void rejectsNullInput() {
        var pipeline = newPipeline(registry);
        assertThatNullPointerException().isThrownBy(() -> pipeline.validate(null));
    }
}
