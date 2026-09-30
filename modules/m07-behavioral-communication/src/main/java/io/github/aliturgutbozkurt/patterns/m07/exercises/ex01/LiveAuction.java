package io.github.aliturgutbozkurt.patterns.m07.exercises.ex01;

import java.util.Optional;
import java.util.function.Consumer;

/** Assignment 01 — your live auction (the Observer subject). */
public class LiveAuction implements Auction {

    private final long startingPriceCents;
    private final long minIncrementCents;
    private final Consumer<RuntimeException> errorHandler;

    public LiveAuction(long startingPriceCents, long minIncrementCents, Consumer<RuntimeException> errorHandler) {
        // TODO(ex01): validate the arguments (a null error handler is an error).
        this.startingPriceCents = startingPriceCents;
        this.minIncrementCents = minIncrementCents;
        this.errorHandler = errorHandler;
    }

    @Override
    public Subscription subscribe(Consumer<? super AuctionEvent> listener) {
        // TODO(ex01): register the listener; the returned Subscription must remove exactly this registration.
        throw new UnsupportedOperationException("TODO(ex01): implement subscribe(Consumer)");
    }

    @Override
    public <E extends AuctionEvent> Subscription subscribe(Class<E> type, Consumer<? super E> listener) {
        // TODO(ex01): deliver only events that are instances of type (hint: type.isInstance / type.cast).
        throw new UnsupportedOperationException("TODO(ex01): implement subscribe(Class, Consumer)");
    }

    @Override
    public void placeBid(Bid bid) {
        // TODO(ex01): closed? below starting price? below highest + minimum increment? Then publish the event.
        throw new UnsupportedOperationException("TODO(ex01): implement placeBid(Bid)");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("TODO(ex01): implement close()");
    }

    @Override
    public Optional<Bid> highestBid() {
        throw new UnsupportedOperationException("TODO(ex01): implement highestBid()");
    }
}
