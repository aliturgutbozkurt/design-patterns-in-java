package io.github.aliturgutbozkurt.patterns.m10.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. What one provider contributed to a comparison. */
public sealed interface ProviderResult {

    /** The provider answered in time. */
    record Priced(Quote quote) implements ProviderResult {
        public Priced {
            Objects.requireNonNull(quote, "quote");
        }
    }

    /** The provider threw; {@code reason} is the exception's message. */
    record Failed(String provider, String reason) implements ProviderResult {
        public Failed {
            Objects.requireNonNull(provider, "provider");
        }
    }

    /** The provider had not answered when the deadline expired, and was cancelled. */
    record TimedOut(String provider) implements ProviderResult {
        public TimedOut {
            Objects.requireNonNull(provider, "provider");
        }
    }
}
