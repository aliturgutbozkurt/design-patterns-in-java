package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Account;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Money;
import java.util.Objects;
import java.util.Optional;

/**
 * Application service: implements the inbound port using only outbound ports and the domain. It never sees an
 * adapter class, so it is tested with hand-written fakes.
 *
 * @see "m11 lesson, section Ports and Adapters"
 */
public final class TransferService implements TransferMoneyUseCase {

    private final LoadAccountPort loadAccount;
    private final SaveAccountPort saveAccount;
    private final Money limit;

    public TransferService(LoadAccountPort loadAccount, SaveAccountPort saveAccount, Money limit) {
        this.loadAccount = Objects.requireNonNull(loadAccount, "loadAccount");
        this.saveAccount = Objects.requireNonNull(saveAccount, "saveAccount");
        this.limit = Objects.requireNonNull(limit, "limit");
    }

    @Override
    public TransferResult transfer(TransferCommand command) {
        Objects.requireNonNull(command, "command");
        if (command.from().equals(command.to())) {
            return new TransferResult.Rejected("same source and target account");
        }
        if (command.amount().isZero()) {
            return new TransferResult.Rejected("amount must be positive");
        }
        if (command.amount().compareTo(limit) > 0) {
            return new TransferResult.Rejected("amount exceeds the limit of " + limit);
        }
        Optional<Account> source = loadAccount.load(command.from());
        if (source.isEmpty()) {
            return new TransferResult.Rejected("unknown account: " + command.from());
        }
        Optional<Account> target = loadAccount.load(command.to());
        if (target.isEmpty()) {
            return new TransferResult.Rejected("unknown account: " + command.to());
        }
        if (!source.get().canWithdraw(command.amount())) {
            return new TransferResult.Rejected("insufficient funds");
        }
        source.get().withdraw(command.amount());
        target.get().deposit(command.amount());
        saveAccount.save(source.get());
        saveAccount.save(target.get());
        return new TransferResult.Transferred(command.from(), command.to(), command.amount());
    }
}
