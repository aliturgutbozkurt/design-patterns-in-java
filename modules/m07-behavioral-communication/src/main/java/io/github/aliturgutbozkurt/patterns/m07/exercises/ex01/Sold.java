package io.github.aliturgutbozkurt.patterns.m07.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. The auction closed and the highest bid wins. */
public record Sold(Bid winningBid) implements AuctionEvent {

    public Sold {
        Objects.requireNonNull(winningBid, "winningBid");
    }
}
