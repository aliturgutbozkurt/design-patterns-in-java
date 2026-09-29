package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.jdk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class JdkTemplateMethodTest {

    @Test
    void countdownInheritsListBehaviourFromGetAndSize() {
        var countdown = new Countdown(3);
        var seen = new ArrayList<Integer>();
        for (int n : countdown) {
            seen.add(n);
        }
        assertThat(seen).containsExactly(3, 2, 1);
        assertThat(countdown.contains(2)).isTrue();
        assertThat(countdown.contains(4)).isFalse();
        assertThat(countdown.indexOf(1)).isEqualTo(2);
        assertThat(countdown.subList(1, 3)).containsExactly(2, 1);
        assertThat(countdown).isEqualTo(List.of(3, 2, 1));
        assertThat(countdown.toString()).isEqualTo("[3, 2, 1]");
    }

    @Test
    void countdownIsReadOnly() {
        assertThatThrownBy(() -> new Countdown(3).add(0)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void countdownChecksItsIndex() {
        assertThatThrownBy(() -> new Countdown(3).get(3)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatIllegalArgumentException().isThrownBy(() -> new Countdown(-1));
    }

    @Test
    void alphabetStreamSupportsReadAllBytesWithOnlyReadImplemented() throws IOException {
        try (var in = new AlphabetStream(5)) {
            assertThat(new String(in.readAllBytes(), StandardCharsets.US_ASCII)).isEqualTo("abcde");
        }
    }

    @Test
    void alphabetStreamSupportsTransferTo() throws IOException {
        var out = new ByteArrayOutputStream();
        try (var in = new AlphabetStream(26)) {
            assertThat(in.transferTo(out)).isEqualTo(26);
        }
        assertThat(out.toString(StandardCharsets.US_ASCII)).isEqualTo("abcdefghijklmnopqrstuvwxyz");
    }

    @Test
    void alphabetStreamSupportsBulkReadIntoAnArray() throws IOException {
        byte[] buffer = new byte[4];
        try (var in = new AlphabetStream(3)) {
            assertThat(in.read(buffer, 0, 4)).isEqualTo(3);
            assertThat(in.read()).isEqualTo(-1);
        }
        assertThat(new String(buffer, 0, 3, StandardCharsets.US_ASCII)).isEqualTo("abc");
    }

    @Test
    void demoPrintsWhatTheInheritedMethodsDo() {
        assertThat(Console.capture(() -> JdkTemplateMethodDemo.main(new String[0]))).isEqualTo("""
                for-each over Countdown(5): 5 4 3 2 1
                contains(3)=true indexOf(1)=4 subList(1, 3)=[4, 3]
                equals(List.of(5, 4, 3, 2, 1))=true
                add(0) -> UnsupportedOperationException
                readAllBytes(): abcdefghij
                transferTo(): 26 bytes, abcdefghijklmnopqrstuvwxyz
                """);
    }
}
