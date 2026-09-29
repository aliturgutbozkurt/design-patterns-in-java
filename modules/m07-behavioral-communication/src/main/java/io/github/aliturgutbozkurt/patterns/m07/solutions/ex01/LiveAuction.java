package io.github.aliturgutbozkurt.patterns.m07.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m07.exercises.ex01.Auction;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex01.AuctionEvent;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex01.Bid;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex01.BidPlaced;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex01.BidRejected;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex01.Sold;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex01.Subscription;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex01.Unsold;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Reference solution for assignment 01: an Observer subject with typed, unsubscribable listeners.
 *
 * @see "m07 lesson, section Observer — Modern Java 27"
 */
public class LiveAuction implements Auction {

    /** One object per subscribe call, removed by identity, so equal listeners never unsubscribe each other. */
    private static final class Registration {
        private final Consumer<? super AuctionEvent> listener;

        private Registration(Consumer<? super AuctionEvent> listener) {
            this.listener = listener;
        }
    }

    private final long startingPriceCents;
    private final long minIncrementCents;
    private final Consumer<RuntimeException> errorHandler;
    // Copy-on-write: a delivery iterates over a snapshot, so (un)subscribing during it affects only the next event.
    private final List<Registration> registrations = new CopyOnWriteArrayList<>();
    private Bid highest;
    private boolean closed;

    public LiveAuction(long startingPriceCents, long minIncrementCents, Consumer<RuntimeException> errorHandler) {
        if (startingPriceCents <= 0 || minIncrementCents <= 0) {
            throw new IllegalArgumentException("prices must be positive");
        }
        this.startingPriceCents = startingPriceCents;
        this.minIncrementCents = minIncrementCents;
        this.errorHandler = Objects.requireNonNull(errorHandler, "errorHandler");
    }

    @Override
    public Subscription subscribe(Consumer<? super AuctionEvent> listener) {
        var registration = new Registration(Objects.requireNonNull(listener, "listener"));
        registrations.add(registration);
        return () -> registrations.remove(registration); // a second remove finds nothing: idempotent
    }

    @Override
    public <E extends AuctionEvent> Subscription subscribe(Class<E> type, Consumer<? super E> listener) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(listener, "listener");
        return subscribe(event -> {
            if (type.isInstance(event)) {
                listener.accept(type.cast(event));
            }
        });
    }

    @Override
    public void placeBid(Bid bid) {
        Objects.requireNonNull(bid, "bid");
        if (closed) {
            publish(new BidRejected(bid, "auction closed"));
        } else if (highest == null && bid.amountCents() < startingPriceCents) {
            publish(new BidRejected(bid, "below starting price"));
        } else if (highest != null && bid.amountCents() < highest.amountCents() + minIncrementCents) {
            publish(new BidRejected(bid, "below minimum increment"));
        } else {
            highest = bid;
            publish(new BidPlaced(bid));
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        publish(highest == null ? new Unsold() : new Sold(highest));
    }

    @Override
    public Optional<Bid> highestBid() {
        return Optional.ofNullable(highest);
    }

    private void publish(AuctionEvent event) {
        for (Registration registration : registrations) {
            try {
                registration.listener.accept(event);
            } catch (RuntimeException e) {
                errorHandler.accept(e);
            }
        }
    }
}
