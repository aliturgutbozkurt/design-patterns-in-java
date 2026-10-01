package io.github.aliturgutbozkurt.patterns.m09.examples.result.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Err;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Ok;
import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ResultTest {

    private static final Result<Integer, String> OK = Result.ok(4);
    private static final Result<Integer, String> ERR = Result.err("boom");

    /** Three sample functions for the monad laws: one that always succeeds and two that can fail. */
    private static final Function<Integer, Result<Integer, String>> HALF =
            n -> n % 2 == 0 ? Result.ok(n / 2) : Result.err("odd: " + n);
    private static final Function<Integer, Result<Integer, String>> POSITIVE =
            n -> n > 0 ? Result.ok(n) : Result.err("not positive: " + n);

    @Test
    void mapAndFlatMapNeverCallTheirFunctionOnErr() {
        var calls = new AtomicInteger();
        assertThat(ERR.map(n -> calls.incrementAndGet() + n)).isEqualTo(ERR);
        assertThat(ERR.flatMap(n -> {
            calls.incrementAndGet();
            return Result.<Integer, String>ok(n);
        })).isEqualTo(ERR);
        assertThat(calls).hasValue(0);
    }

    @Test
    void mapErrorNeverCallsItsFunctionOnOk() {
        var calls = new AtomicInteger();
        assertThat(OK.mapError(e -> calls.incrementAndGet() + e)).isEqualTo(new Ok<>(4));
        assertThat(calls).hasValue(0);
        assertThat(ERR.mapError(String::length)).isEqualTo(new Err<>(4));
    }

    @Test
    void mapTransformsTheValueOnOk() {
        assertThat(OK.map(n -> n * 10)).isEqualTo(new Ok<>(40));
        assertThat(OK.flatMap(HALF)).isEqualTo(new Ok<>(2));
        assertThat(Result.<Integer, String>ok(3).flatMap(HALF)).isEqualTo(new Err<>("odd: 3"));
    }

    @Test
    void foldPicksTheMatchingBranch() {
        String ok = OK.fold(v -> "ok:" + v, e -> "err:" + e);
        String err = ERR.fold(v -> "ok:" + v, e -> "err:" + e);
        assertThat(ok).isEqualTo("ok:4");
        assertThat(err).isEqualTo("err:boom");
    }

    @Test
    void switchOverRecordPatternsIsExhaustive() {
        String text = switch (ERR) {
            case Ok(var v) -> "value " + v;
            case Err(var e) -> "error " + e;
        };
        assertThat(text).isEqualTo("error boom");
    }

    @ParameterizedTest
    @ValueSource(ints = {-3, 0, 1, 4, 7, 12})
    void leftIdentityHolds(int a) {
        assertThat(Result.<Integer, String>ok(a).flatMap(HALF)).isEqualTo(HALF.apply(a));
        assertThat(Result.<Integer, String>ok(a).flatMap(POSITIVE)).isEqualTo(POSITIVE.apply(a));
    }

    @ParameterizedTest
    @ValueSource(ints = {-3, 0, 1, 4, 7, 12})
    void rightIdentityHolds(int a) {
        for (Result<Integer, String> m : List.of(Result.<Integer, String>ok(a), Result.<Integer, String>err("e" + a))) {
            assertThat(m.flatMap(Result::ok)).isEqualTo(m);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-4, -3, 0, 1, 4, 7, 12})
    void associativityHolds(int a) {
        for (Result<Integer, String> m : List.of(Result.<Integer, String>ok(a), Result.<Integer, String>err("e" + a))) {
            assertThat(m.flatMap(HALF).flatMap(POSITIVE)).isEqualTo(m.flatMap(x -> HALF.apply(x).flatMap(POSITIVE)));
        }
    }

    @Test
    void okAndErrRejectNull() {
        assertThatNullPointerException().isThrownBy(() -> new Ok<Integer, String>(null)).withMessage("value");
        assertThatNullPointerException().isThrownBy(() -> new Err<Integer, String>(null)).withMessage("error");
    }

    @Test
    void attemptTurnsAThrownCheckedExceptionIntoErr() {
        Result<String, String> failed = Result.attempt(() -> {
            throw new IOException("disk full");
        }, Throwable::getMessage);
        Result<String, String> worked = Result.attempt(() -> "saved", Throwable::getMessage);
        assertThat(failed).isEqualTo(new Err<>("disk full"));
        assertThat(worked).isEqualTo(new Ok<>("saved"));
    }

    @Test
    void orElseAndOrElseGetGiveTheValueOrAFallback() {
        assertThat(OK.orElse(0)).isEqualTo(4);
        assertThat(ERR.orElse(0)).isZero();
        assertThat(ERR.orElseGet(String::length)).isEqualTo(4);
    }

    @Test
    void toOptionalOfErrIsEmpty() {
        assertThat(OK.toOptional()).contains(4);
        assertThat(ERR.toOptional()).isEmpty();
    }

    @Test
    void sequenceReturnsTheFirstErrOrAllValuesInOrder() {
        assertThat(Results.sequence(List.of(Result.<Integer, String>ok(1), Result.ok(2), Result.ok(3))))
                .isEqualTo(new Ok<>(List.of(1, 2, 3)));
        assertThat(Results.sequence(List.of(Result.<Integer, String>ok(1), Result.err("first"), Result.err("second"))))
                .isEqualTo(new Err<>("first"));
        assertThat(Results.<Integer, String>sequence(List.of())).isEqualTo(new Ok<>(List.of()));
    }

    @Test
    void partitionKeepsInputOrderInBothLists() {
        var parts = Results.partition(List.of(
                Result.<Integer, String>err("a"), Result.ok(1), Result.err("b"), Result.ok(2), Result.ok(3)));
        assertThat(parts).isEqualTo(new Partitioned<>(List.of(1, 2, 3), List.of("a", "b")));
        assertThat(parts.oks()).isUnmodifiable();
        assertThat(parts.errors()).isUnmodifiable();
    }

    @Test
    void demoPrintsBothTracks() {
        assertThat(Console.capture(() -> ResultBasicsDemo.main(new String[0]))).isEqualTo("""
                -- parse, then check the range (flatMap)
                "3"    -> Ok[value=3]
                " 12 " -> Ok[value=12]
                "abc"  -> Err[error=not a number: "abc"]
                "150"  -> Err[error=out of range 1..99: 150]
                -- price the good ones (map), then leave the railway (fold)
                "3"    -> 3 x 2.50 = 750 cents
                " 12 " -> 12 x 2.50 = 3000 cents
                "abc"  -> rejected: not a number: "abc"
                "150"  -> rejected: out of range 1..99: 150
                -- switch over Ok(var v) / Err(var e)
                "abc"  -> ask the customer again (not a number: "abc")
                -- many results at once
                sequence(3, 12)      = Ok[value=[3, 12]]
                sequence(all four)   = Err[error=not a number: "abc"]
                partition(all four)  = Partitioned[oks=[3, 12], errors=[not a number: "abc", out of range 1..99: 150]]
                """);
    }
}
