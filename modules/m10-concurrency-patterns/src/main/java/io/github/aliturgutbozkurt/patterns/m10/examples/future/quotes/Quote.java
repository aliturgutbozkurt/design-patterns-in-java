package io.github.aliturgutbozkurt.patterns.m10.examples.future.quotes;

import java.util.Locale;
import java.util.Objects;

/**
 * An insurance premium offered by one provider.
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
public record Quote(String provider, long premiumCents) {

    public Quote {
        Objects.requireNonNull(provider, "provider");
    }

    @Override
    public String toString() {
        return premiumCents == Long.MAX_VALUE
                ? provider + " FALLBACK"
                : String.format(Locale.ROOT, "%s %d.%02d", provider, premiumCents / 100, premiumCents % 100);
    }
}
