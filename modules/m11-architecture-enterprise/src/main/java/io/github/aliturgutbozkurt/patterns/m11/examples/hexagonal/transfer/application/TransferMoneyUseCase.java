package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application;

/**
 * Inbound port: what the application offers to the outside world. Controllers depend on this interface, never on
 * {@link TransferService}.
 *
 * @see "m11 lesson, section Ports and Adapters"
 */
@FunctionalInterface
public interface TransferMoneyUseCase {

    /** Moves money between two accounts; business refusals are a {@link TransferResult.Rejected}, not exceptions. */
    TransferResult transfer(TransferCommand command);
}
