package io.github.aliturgutbozkurt.patterns.m10.exercises.ex01;

/**
 * GIVEN — do not modify. A remote price source. {@link #quote} is a blocking call: it may take long, hang, or throw.
 * A well-behaved provider stops when its thread is interrupted.
 */
public interface PriceProvider {

    String name();

    Quote quote(String sku) throws Exception;
}
