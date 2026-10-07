package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.cli.CommandLine;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * F11 — the command-line adapter: help, a scripted session compared verbatim with {@code cli-session.txt}, error and
 * usage responses (SPEC-capstone "Output formats").
 */
public abstract class CliAcceptance extends AcceptanceContract {

    private CommandLine cli() {
        return shop().cli();
    }

    @Test
    void helpListsEveryCommand() {
        assertThat(cli().execute("help")).isEqualTo(ExpectedOutput.read("cli-help.txt").stripTrailing());
    }

    @Test
    void scriptedSessionProducesTheExpectedTranscript() {
        List<String> commands = new ArrayList<>();
        List<String> expected = new ArrayList<>();
        StringBuilder response = null;
        for (String line : ExpectedOutput.read("cli-session.txt").split("\n")) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("> ")) {
                if (response != null) {
                    expected.add(response.toString());
                }
                commands.add(line.substring(2));
                response = new StringBuilder();
            } else if (response != null) {
                response.append(response.isEmpty() ? "" : "\n").append(line);
            }
        }
        expected.add(String.valueOf(response));

        for (int i = 0; i < commands.size(); i++) {
            assertThat(cli().execute(commands.get(i))).as("> %s", commands.get(i)).isEqualTo(expected.get(i));
        }
    }

    @Test
    void unknownCommandIsReported() {
        assertThat(cli().execute("frobnicate")).isEqualTo("ERROR unknown command: frobnicate");
        assertThat(cli().execute("cart fly cart-1")).isEqualTo("ERROR unknown command: cart fly");
        assertThat(cli().execute("report weekly")).isEqualTo("ERROR unknown command: report weekly");
    }

    @Test
    void malformedArgumentsPrintUsage() {
        assertThat(cli().execute("cart add cart-1 BOK-001")).isEqualTo("USAGE cart add <cart> <sku> <qty>");
        assertThat(cli().execute("cart add cart-1 BOK-001 two")).isEqualTo("USAGE cart add <cart> <sku> <qty>");
        assertThat(cli().execute("cart add cart-1 bok-1 2")).isEqualTo("USAGE cart add <cart> <sku> <qty>");
        assertThat(cli().execute("product list GAMES")).isEqualTo("USAGE product list [CATEGORY]");
        assertThat(cli().execute("order show 42")).isEqualTo("USAGE order show <order>");
        assertThat(cli().execute("order cancel order-1")).isEqualTo("USAGE order cancel <order> <reason…>");
        assertThat(cli().execute("checkout cart-1 tok_visa_ok Alice Doe;Bagdat Cd. 1"))
                .isEqualTo("USAGE checkout <cart> <card-token> <recipient>;<street>;<city>;<postal-code>");
        assertThat(cli().execute("report sales 2026-11-16")).isEqualTo("USAGE report sales <from> <to> [--csv]");
        assertThat(cli().execute("report top many")).isEqualTo("USAGE report top <n> [--csv]");
        assertThat(cli().execute("report inventory --pdf")).isEqualTo("USAGE report inventory [--csv]");

        assertThat(cli().execute("cart add cart-9 BOK-001 1")).as("a use-case exception is an error, not a usage")
                .isEqualTo("ERROR unknown cart: cart-9");
        assertThat(cli().execute("order show order-9")).isEqualTo("ERROR unknown order: order-9");
    }
}
