package io.github.aliturgutbozkurt.patterns.m07.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. A bid was accepted and is now the highest one. */
public record BidPlaced(Bid bid) implements AuctionEvent {

    public BidPlaced {
        Objects.requireNonNull(bid, "bid");
    }
}
