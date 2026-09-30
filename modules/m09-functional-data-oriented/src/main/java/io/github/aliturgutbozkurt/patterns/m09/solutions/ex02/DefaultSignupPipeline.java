package io.github.aliturgutbozkurt.patterns.m09.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Country;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.RawSignup;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Referral;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Referral.NoReferral;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Referral.ReferredBy;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Result;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Signup;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.InvalidFormat;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.InvalidReferral;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.Missing;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.OutOfRange;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.TooShort;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.UnknownCountry;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupError.UsernameTaken;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupPipeline;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.UserRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Reference solution: five field parsers that each return a {@code Result}; stage 1 collects all their errors, stage 2
 * is a fail-fast chain of {@code flatMap}s that is reached only with valid input.
 *
 * @see "m09 lesson, section Optional and Result — errors as values"
 */
public class DefaultSignupPipeline implements SignupPipeline {

    private static final Pattern USERNAME = Pattern.compile("[a-z0-9_]{3,20}");
    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 120;

    private final UserRegistry registry;

    public DefaultSignupPipeline(UserRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public Result<Signup, List<SignupError>> validate(RawSignup raw) {
        Objects.requireNonNull(raw, "raw");
        Result<String, SignupError> username = username(raw.username());
        Result<String, SignupError> email = email(raw.email());
        Result<Integer, SignupError> age = age(raw.age());
        Result<Country, SignupError> country = country(raw.country());
        Result<Referral, SignupError> referral = Result.ok(referral(raw.referralCode()));

        List<SignupError> errors = Stream.of(username, email, age, country, referral)
                .flatMap(r -> r.fold(_ -> Stream.<SignupError>empty(), Stream::of))
                .toList();
        if (!errors.isEmpty()) {
            return Result.err(errors);
        }
        Result<Signup, List<SignupError>> signup = username
                .flatMap(u -> email.flatMap(e -> age.flatMap(a -> country.flatMap(c -> referral.map(
                        r -> new Signup(u, e, a, c, r))))))
                .mapError(List::of);
        return signup.flatMap(this::usernameFree).flatMap(this::referralValid);
    }

    private static Result<String, SignupError> username(String text) {
        if (blank(text)) {
            return Result.err(new Missing("username"));
        }
        String name = text.strip().toLowerCase(Locale.ROOT);
        if (name.length() < 3) {
            return Result.err(new TooShort("username", 3));
        }
        return USERNAME.matcher(name).matches() ? Result.ok(name) : Result.err(new InvalidFormat("username"));
    }

    private static Result<String, SignupError> email(String text) {
        if (blank(text)) {
            return Result.err(new Missing("email"));
        }
        String address = text.strip().toLowerCase(Locale.ROOT);
        return EMAIL.matcher(address).matches() ? Result.ok(address) : Result.err(new InvalidFormat("email"));
    }

    private static Result<Integer, SignupError> age(String text) {
        if (blank(text)) {
            return Result.err(new Missing("age"));
        }
        Result<Integer, SignupError> number =
                Result.attempt(() -> Integer.parseInt(text.strip(), 10), _ -> new InvalidFormat("age"));
        return number.flatMap(n -> n >= MIN_AGE && n <= MAX_AGE
                ? Result.ok(n)
                : Result.err(new OutOfRange("age", MIN_AGE, MAX_AGE)));
    }

    private static Result<Country, SignupError> country(String text) {
        if (blank(text)) {
            return Result.err(new Missing("country"));
        }
        String code = text.strip().toUpperCase(Locale.ROOT);
        return Arrays.stream(Country.values())
                .filter(c -> c.name().equals(code))
                .findFirst()
                .<Result<Country, SignupError>>map(Result::ok)
                .orElseGet(() -> Result.err(new UnknownCountry(text.strip())));
    }

    private static Referral referral(String text) {
        return blank(text) ? new NoReferral() : new ReferredBy(text.strip().toUpperCase(Locale.ROOT));
    }

    private Result<Signup, List<SignupError>> usernameFree(Signup signup) {
        return registry.isTaken(signup.username())
                ? Result.err(List.of(new UsernameTaken(signup.username())))
                : Result.ok(signup);
    }

    private Result<Signup, List<SignupError>> referralValid(Signup signup) {
        return switch (signup.referral()) {
            case NoReferral _ -> Result.ok(signup);
            case ReferredBy(var code) -> registry.isValidReferral(code)
                    ? Result.ok(signup)
                    : Result.err(List.of(new InvalidReferral(code)));
        };
    }

    private static boolean blank(String text) {
        return text == null || text.isBlank();
    }
}
