package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferCommand;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferResult;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferResult.Rejected;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferResult.Transferred;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferService;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Account;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.AccountId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Money;
import org.junit.jupiter.api.Test;

class TransferServiceTest {

    private final FakeAccounts accounts = new FakeAccounts().with("A-1", "100.00").with("A-2", "20.00");
    private final TransferService service = new TransferService(accounts, accounts, Money.of("1000.00"));

    private TransferResult transfer(String from, String to, String amount) {
        return service.transfer(new TransferCommand(new AccountId(from), new AccountId(to), Money.of(amount)));
    }

    @Test
    void successfulTransferMovesTheAmountAndSavesBothAccounts() {
        assertThat(transfer("A-1", "A-2", "25.00"))
                .isEqualTo(new Transferred(new AccountId("A-1"), new AccountId("A-2"), Money.of("25.00")));
        assertThat(accounts.saved).containsExactly("A-1=75.00", "A-2=45.00");
    }

    @Test
    void insufficientFundsIsRejectedAndNeitherAccountIsSaved() {
        assertThat(transfer("A-2", "A-1", "20.01")).isEqualTo(new Rejected("insufficient funds"));
        assertThat(accounts.saved).isEmpty();
        assertThat(accounts.balanceOf("A-2")).isEqualTo(Money.of("20.00"));
    }

    @Test
    void wholeBalanceCanBeTransferred() {
        assertThat(transfer("A-2", "A-1", "20.00")).isInstanceOf(Transferred.class);
        assertThat(accounts.balanceOf("A-2")).isEqualTo(Money.of("0.00"));
    }

    @Test
    void sameSourceAndTargetIsRejected() {
        assertThat(transfer("A-1", "A-1", "1.00")).isEqualTo(new Rejected("same source and target account"));
        assertThat(accounts.saved).isEmpty();
    }

    @Test
    void amountAboveTheInjectedLimitIsRejected() {
        var strict = new TransferService(accounts, accounts, Money.of("10.00"));
        TransferResult result = strict.transfer(
                new TransferCommand(new AccountId("A-1"), new AccountId("A-2"), Money.of("10.01")));
        assertThat(result).isEqualTo(new Rejected("amount exceeds the limit of 10.00"));
        assertThat(accounts.saved).isEmpty();
    }

    @Test
    void zeroAmountIsRejected() {
        assertThat(transfer("A-1", "A-2", "0.00")).isEqualTo(new Rejected("amount must be positive"));
    }

    @Test
    void unknownAccountIsRejectedWithItsId() {
        assertThat(transfer("A-9", "A-2", "1.00")).isEqualTo(new Rejected("unknown account: A-9"));
        assertThat(transfer("A-1", "A-8", "1.00")).isEqualTo(new Rejected("unknown account: A-8"));
        assertThat(accounts.saved).isEmpty();
    }

    @Test
    void accountGuardsItsOwnInvariant() {
        var account = new Account(new AccountId("A-1"), Money.of("5.00"));
        assertThatIllegalStateException().isThrownBy(() -> account.withdraw(Money.of("5.01")))
                .withMessage("insufficient funds");
        assertThat(account.balance()).isEqualTo(Money.of("5.00"));
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> service.transfer(null));
        assertThatNullPointerException().isThrownBy(() -> new TransferService(null, accounts, Money.of("1.00")));
        assertThatNullPointerException()
                .isThrownBy(() -> new TransferCommand(null, new AccountId("A-1"), Money.of("1.00")));
    }
}
