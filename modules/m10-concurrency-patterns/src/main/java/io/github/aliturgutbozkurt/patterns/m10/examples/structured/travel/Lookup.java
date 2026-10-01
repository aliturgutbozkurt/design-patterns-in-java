package io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel;

/**
 * A blocking remote look-up for one destination (flights, hotels, weather). It may throw anything; the scope
 * decides what a failure means for the whole plan.
 *
 * @param <T> what the look-up returns
 * @see "m10 lesson, section Structured Concurrency"
 */
@FunctionalInterface
public interface Lookup<T> {

    T find(String destination) throws Exception;
}
