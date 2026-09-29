package io.github.aliturgutbozkurt.patterns.m07.exercises.ex01;

/** GIVEN — do not modify. Everything an {@link Auction} announces to its listeners. */
public sealed interface AuctionEvent permits BidPlaced, BidRejected, Sold, Unsold {}
