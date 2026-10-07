package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.in.cli;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

/**
 * The arguments after the command words: as tokens, and as the raw rest of the line (for free text such as a reason
 * or an address). Every parser throws {@link UsageException} on bad input.
 *
 * @param tokens the whitespace-separated arguments
 * @param rest   the arguments as typed
 */
record CliArgs(List<String> tokens, String rest) {

    CliArgs {
        tokens = List.copyOf(tokens);
    }

    /** Requires exactly {@code count} arguments. */
    CliArgs exactly(int count) {
        if (tokens.size() != count) {
            throw new UsageException();
        }
        return this;
    }

    /** Requires {@code count} arguments plus an optional {@code --csv}; returns whether it was given. */
    boolean withCsvFlag(int count) {
        if (tokens.size() == count + 1 && tokens.getLast().equals("--csv")) {
            return true;
        }
        exactly(count);
        return false;
    }

    String text(int index) {
        return tokens.get(index);
    }

    int integer(int index) {
        return parse(() -> Integer.parseInt(tokens.get(index)));
    }

    Sku sku(int index) {
        return parse(() -> new Sku(tokens.get(index)));
    }

    CartId cart(int index) {
        return new CartId(tokens.get(index));
    }

    OrderId order(int index) {
        return parse(() -> new OrderId(tokens.get(index)));
    }

    CustomerId customer(int index) {
        return new CustomerId(tokens.get(index));
    }

    LocalDate date(int index) {
        return parse(() -> LocalDate.parse(tokens.get(index)));
    }

    /** {@code recipient;street;city;postal-code} — exactly four fields, each trimmed. */
    static Address address(String text) {
        String[] fields = text.split(";", -1);
        if (fields.length != 4) {
            throw new UsageException();
        }
        return new Address(fields[0].strip(), fields[1].strip(), fields[2].strip(), fields[3].strip());
    }

    /** Runs a parser and turns its rejection of the input into a usage error. */
    static <T> T parse(Supplier<T> parser) {
        try {
            return parser.get();
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new UsageException();
        }
    }
}
