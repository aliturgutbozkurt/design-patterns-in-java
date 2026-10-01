package io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory;

import io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory.Referral.Direct;
import io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory.Referral.ReferredBy;
import java.util.Objects;
import java.util.Optional;

/**
 * A customer. The referral is a sealed {@link Referral} component; {@link #referrer()} offers an {@code Optional}
 * view of it, as a return type only.
 *
 * @see "m09 lesson, section Optional and Result — Optional as a return type"
 */
public record Customer(CustomerId id, String name, String email, Referral referral) {

    public Customer {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(email, "email");
        Objects.requireNonNull(referral, "referral");
    }

    /** Who referred this customer, if anyone. */
    public Optional<CustomerId> referrer() {
        return switch (referral) {
            case Direct _ -> Optional.empty();
            case ReferredBy(var referrer) -> Optional.of(referrer);
        };
    }
}
