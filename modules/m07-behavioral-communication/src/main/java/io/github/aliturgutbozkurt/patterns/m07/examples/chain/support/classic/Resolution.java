package io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Who resolved a ticket and every level it visited on the way, in order (the resolver is the last one).
 *
 * @see "m07 lesson, section Chain of Responsibility"
 */
public record Resolution(String ticketId, String handledBy, List<String> escalationPath) {

    public Resolution {
        Objects.requireNonNull(ticketId, "ticketId");
        Objects.requireNonNull(handledBy, "handledBy");
        escalationPath = List.copyOf(escalationPath);
    }

    /** The same resolution, reached after passing through {@code earlierLevels} first. */
    public Resolution escalatedFrom(List<String> earlierLevels) {
        return new Resolution(ticketId, handledBy, Stream.concat(earlierLevels.stream(), escalationPath.stream()).toList());
    }
}
