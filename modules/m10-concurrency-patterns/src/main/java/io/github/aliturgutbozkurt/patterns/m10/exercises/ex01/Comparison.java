package io.github.aliturgutbozkurt.patterns.m10.exercises.ex01;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** GIVEN — do not modify. The result of comparing one sku: one entry per provider, in provider order. */
public record Comparison(String sku, List<ProviderResult> results) {

    public Comparison {
        Objects.requireNonNull(sku, "sku");
        results = List.copyOf(results);
    }

    /** The lowest {@link ProviderResult.Priced} quote; on a tie the earlier provider wins. Empty if none. */
    public Optional<Quote> cheapest() {
        Quote best = null;
        for (ProviderResult result : results) {
            if (result instanceof ProviderResult.Priced(Quote quote)
                    && (best == null || quote.priceCents() < best.priceCents())) {
                best = quote;
            }
        }
        return Optional.ofNullable(best);
    }
}
