package io.github.aliturgutbozkurt.patterns.m07.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

/** Assignment 01 — live auction notifications. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    private static final long START = 100_00;
    private static final long INCREMENT = 5_00;

    /** A new auction with the given starting price, minimum increment (both in cents) and error handler. */
    protected abstract Auction newAuction(
            long startingPriceCents, long minIncrementCents, Consumer<RuntimeException> errorHandler);

    private final List<RuntimeException> errors = new ArrayList<>();
    private final List<AuctionEvent> events = new ArrayList<>();

    private Auction auction() {
        return newAuction(START, INCREMENT, errors::add);
    }

    @Test
    void acceptsFirstBidAtStartingPrice() {
        Auction auction = auction();
        auction.subscribe(events::add);
        var bid = new Bid("ada", 100_00);
        auction.placeBid(bid);
        assertThat(events).containsExactly(new BidPlaced(bid));
        assertThat(auction.highestBid()).contains(bid);
    }

    @Test
    void rejectsBidBelowStartingPrice() {
        Auction auction = auction();
        auction.subscribe(events::add);
        var bid = new Bid("ada", 99_99);
        auction.placeBid(bid);
        assertThat(events).containsExactly(new BidRejected(bid, "below starting price"));
        assertThat(auction.highestBid()).isEmpty();
    }

    @Test
    void rejectsBidBelowMinimumIncrement() {
        Auction auction = auction();
        auction.subscribe(events::add);
        var first = new Bid("ada", 100_00);
        var tooLow = new Bid("bob", 104_99);
        var enough = new Bid("bob", 105_00);
        auction.placeBid(first);
        auction.placeBid(tooLow);
        auction.placeBid(enough);
        assertThat(events).containsExactly(
                new BidPlaced(first), new BidRejected(tooLow, "below minimum increment"), new BidPlaced(enough));
    }

    @Test
    void highestBidReflectsAcceptedBidsOnly() {
        Auction auction = auction();
        assertThat(auction.highestBid()).isEmpty();
        auction.placeBid(new Bid("ada", 120_00));
        auction.placeBid(new Bid("bob", 121_00));
        assertThat(auction.highestBid()).contains(new Bid("ada", 120_00));
        auction.placeBid(new Bid("cem", 130_00));
        assertThat(auction.highestBid()).contains(new Bid("cem", 130_00));
    }

    @Test
    void listenersNotifiedInSubscriptionOrder() {
        Auction auction = auction();
        List<String> order = new ArrayList<>();
        auction.subscribe(event -> order.add("first"));
        auction.subscribe(BidPlaced.class, event -> order.add("second"));
        auction.subscribe(event -> order.add("third"));
        auction.placeBid(new Bid("ada", 100_00));
        assertThat(order).containsExactly("first", "second", "third");
    }

    @Test
    void typedSubscriptionReceivesOnlyItsEventType() {
        Auction auction = auction();
        List<BidRejected> rejections = new ArrayList<>();
        auction.subscribe(BidRejected.class, rejections::add);
        var low = new Bid("bob", 1_00);
        auction.placeBid(new Bid("ada", 100_00));
        auction.placeBid(low);
        auction.close();
        assertThat(rejections).containsExactly(new BidRejected(low, "below minimum increment"));
    }

    @Test
    void closedSubscriptionReceivesNothing() {
        Auction auction = auction();
        Subscription subscription = auction.subscribe(events::add);
        subscription.close();
        auction.placeBid(new Bid("ada", 100_00));
        auction.close();
        assertThat(events).isEmpty();
    }

    @Test
    void closingASubscriptionTwiceIsHarmless() {
        Auction auction = auction();
        Consumer<AuctionEvent> listener = events::add;
        Subscription first = auction.subscribe(listener);
        auction.subscribe(listener); // the same listener, registered a second time
        first.close();
        first.close(); // must not remove the second registration
        auction.placeBid(new Bid("ada", 100_00));
        assertThat(events).hasSize(1);
    }

    @Test
    void unsubscribingDuringDeliveryTakesEffectFromTheNextEvent() {
        Auction auction = auction();
        List<String> received = new ArrayList<>();
        List<Subscription> second = new ArrayList<>();
        auction.subscribe(event -> {
            received.add("first");
            second.forEach(Subscription::close);
        });
        second.add(auction.subscribe(event -> received.add("second")));
        auction.placeBid(new Bid("ada", 100_00)); // "second" still receives this one
        auction.placeBid(new Bid("bob", 110_00));
        assertThat(received).containsExactly("first", "second", "first");
    }

    @Test
    void subscribingDuringDeliveryTakesEffectFromTheNextEvent() {
        Auction auction = auction();
        List<String> received = new ArrayList<>();
        auction.subscribe(event -> {
            received.add("first");
            if (received.size() == 1) {
                auction.subscribe(late -> received.add("late"));
            }
        });
        auction.placeBid(new Bid("ada", 100_00));
        auction.placeBid(new Bid("bob", 110_00));
        assertThat(received).containsExactly("first", "first", "late");
    }

    @Test
    void failingListenerDoesNotStopOthers() {
        Auction auction = auction();
        var boom = new IllegalStateException("display offline");
        auction.subscribe(event -> {
            throw boom;
        });
        auction.subscribe(events::add);
        auction.placeBid(new Bid("ada", 100_00));
        assertThat(events).hasSize(1);
        assertThat(errors).containsExactly(boom);
    }

    @Test
    void closeWithBidsPublishesSold() {
        Auction auction = auction();
        auction.placeBid(new Bid("ada", 100_00));
        auction.placeBid(new Bid("bob", 150_00));
        auction.subscribe(events::add);
        auction.close();
        assertThat(events).containsExactly(new Sold(new Bid("bob", 150_00)));
    }

    @Test
    void closeWithoutBidsPublishesUnsold() {
        Auction auction = auction();
        auction.subscribe(events::add);
        auction.close();
        assertThat(events).containsExactly(new Unsold());
    }

    @Test
    void closeIsPublishedOnlyOnce() {
        Auction auction = auction();
        auction.subscribe(events::add);
        auction.close();
        auction.close();
        assertThat(events).containsExactly(new Unsold());
    }

    @Test
    void bidsAfterCloseAreRejected() {
        Auction auction = auction();
        auction.placeBid(new Bid("ada", 100_00));
        auction.close();
        auction.subscribe(events::add);
        var late = new Bid("bob", 500_00);
        auction.placeBid(late);
        assertThat(events).containsExactly(new BidRejected(late, "auction closed"));
        assertThat(auction.highestBid()).contains(new Bid("ada", 100_00));
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> newAuction(START, INCREMENT, null));
        Auction auction = auction();
        assertThatNullPointerException().isThrownBy(() -> auction.placeBid(null));
        assertThatNullPointerException().isThrownBy(() -> auction.subscribe(null));
        assertThatNullPointerException().isThrownBy(() -> auction.subscribe(null, event -> {}));
        assertThatNullPointerException().isThrownBy(() -> auction.subscribe(Sold.class, null));
    }
}
