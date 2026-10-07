package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.in.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class CliArgsTest {

    private static CliArgs args(String... tokens) {
        return new CliArgs(List.of(tokens), String.join(" ", tokens));
    }

    @Test
    void parsesTypedArguments() {
        CliArgs args = args("BOK-001", "3", "2026-11-16");

        assertThat(args.sku(0)).isEqualTo(new Sku("BOK-001"));
        assertThat(args.integer(1)).isEqualTo(3);
        assertThat(args.date(2)).isEqualTo(LocalDate.of(2026, 11, 16));
        assertThat(CliArgs.address(" Alice Doe ;Street 1; Istanbul;34710"))
                .isEqualTo(new Address("Alice Doe", "Street 1", "Istanbul", "34710"));
    }

    @Test
    void badInputIsAUsageError() {
        assertThatThrownBy(() -> args("bok-1").sku(0)).isInstanceOf(UsageException.class);
        assertThatThrownBy(() -> args("two").integer(0)).isInstanceOf(UsageException.class);
        assertThatThrownBy(() -> args("2026-13-01").date(0)).isInstanceOf(UsageException.class);
        assertThatThrownBy(() -> args("a", "b").exactly(1)).isInstanceOf(UsageException.class);
        assertThatThrownBy(() -> CliArgs.address("only;three;fields")).isInstanceOf(UsageException.class);
    }

    @Test
    void csvFlagIsOptionalAndOnlyAtTheEnd() {
        assertThat(args("3").withCsvFlag(1)).isFalse();
        assertThat(args("3", "--csv").withCsvFlag(1)).isTrue();
        assertThatThrownBy(() -> args("3", "--pdf").withCsvFlag(1)).isInstanceOf(UsageException.class);
    }
}
