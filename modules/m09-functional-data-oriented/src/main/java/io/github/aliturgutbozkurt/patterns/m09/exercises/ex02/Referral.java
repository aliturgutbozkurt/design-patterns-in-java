package io.github.aliturgutbozkurt.patterns.m09.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. Whether a referral code was given. */
public sealed interface Referral permits Referral.NoReferral, Referral.ReferredBy {

    record NoReferral() implements Referral {}

    record ReferredBy(String code) implements Referral {
        public ReferredBy {
            Objects.requireNonNull(code, "code");
        }
    }
}
