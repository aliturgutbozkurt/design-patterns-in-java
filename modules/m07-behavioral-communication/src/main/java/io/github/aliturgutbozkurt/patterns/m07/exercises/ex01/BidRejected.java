package io.github.aliturgutbozkurt.patterns.m07.exercises.ex01;

import java.util.Objects;

/**
 * GIVEN — do not modify. A bid was refused; {@code reason} is {@code "below starting price"},
 * {@code "below minimum increment"} or {@code "auction closed"}.
 */
public record BidRejected(Bid bid, String reason) implements AuctionEvent {

    public BidRejected {
        Objects.requireNonNull(bid, "bid");
        Objects.requireNonNull(reason, "reason");
    }
}
