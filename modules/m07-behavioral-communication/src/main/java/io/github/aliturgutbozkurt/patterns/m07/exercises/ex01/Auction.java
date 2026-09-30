package io.github.aliturgutbozkurt.patterns.m07.exercises.ex01;

import java.util.Optional;
import java.util.function.Consumer;

/** GIVEN — do not modify. A live auction that announces every change to its listeners (the subject). */
public interface Auction {

    /** Receives every event, in subscription order. */
    Subscription subscribe(Consumer<? super AuctionEvent> listener);

    /** Receives only events of {@code type}. */
    <E extends AuctionEvent> Subscription subscribe(Class<E> type, Consumer<? super E> listener);

    /** Accepts or rejects {@code bid} and publishes {@link BidPlaced} or {@link BidRejected}. */
    void placeBid(Bid bid);

    /** Ends the auction and publishes {@link Sold} or {@link Unsold} — only the first time. */
    void close();

    /** The highest accepted bid so far. */
    Optional<Bid> highestBid();
}
