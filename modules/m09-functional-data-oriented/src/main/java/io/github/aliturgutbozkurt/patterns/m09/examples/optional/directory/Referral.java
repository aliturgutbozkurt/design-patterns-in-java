package io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory;

import java.util.Objects;

/**
 * How a customer found the shop. Absence <em>inside data</em> is modelled as its own case ({@link Direct}), not as a
 * {@code null} or an {@code Optional} field.
 *
 * @see "m09 lesson, section Optional and Result — Optional as a return type"
 */
public sealed interface Referral permits Referral.Direct, Referral.ReferredBy {

    /** Came on their own. */
    record Direct() implements Referral {}

    /** Recommended by another customer. */
    record ReferredBy(CustomerId referrer) implements Referral {
        public ReferredBy {
            Objects.requireNonNull(referrer, "referrer");
        }
    }
}
