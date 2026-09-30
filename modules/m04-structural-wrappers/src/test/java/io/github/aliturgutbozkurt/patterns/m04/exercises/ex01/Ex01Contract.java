package io.github.aliturgutbozkurt.patterns.m04.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Assignment 01 — data-source decorators. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    protected abstract DataSource compression(DataSource wrapped);

    protected abstract DataSource base64(DataSource wrapped);

    private static final byte[] TEXT = "Merhaba dünya! Çalışan bir örnek: ğüşıöç İ".getBytes(StandardCharsets.UTF_8);

    private static byte[] repetitive() {
        return "abc".repeat(10_000).getBytes(StandardCharsets.US_ASCII);
    }

    @Test
    void roundTripsThroughCompression() {
        DataSource source = compression(new InMemoryDataSource());
        source.write(TEXT);
        assertThat(source.read()).isEqualTo(TEXT);
    }

    @Test
    void roundTripsThroughBase64() {
        DataSource source = base64(new InMemoryDataSource());
        source.write(TEXT);
        assertThat(source.read()).isEqualTo(TEXT);
    }

    @Test
    void roundTripsThroughBothInEitherOrder() {
        DataSource base64Outside = base64(compression(new InMemoryDataSource()));
        DataSource compressionOutside = compression(base64(new InMemoryDataSource()));
        base64Outside.write(TEXT);
        compressionOutside.write(TEXT);
        assertThat(base64Outside.read()).isEqualTo(TEXT);
        assertThat(compressionOutside.read()).isEqualTo(TEXT);
    }

    @Test
    void compressionShrinksRepetitiveData() {
        var store = new InMemoryDataSource();
        compression(store).write(repetitive());
        assertThat(store.read().length).isLessThan(repetitive().length / 10);
    }

    @Test
    void base64StoresOnlyBase64Characters() {
        var store = new InMemoryDataSource();
        base64(store).write(TEXT);
        String stored = new String(store.read(), StandardCharsets.US_ASCII);
        assertThat(stored).matches("[A-Za-z0-9+/]+=*").isEqualTo(Base64.getEncoder().encodeToString(TEXT));
    }

    @Test
    void outermostDecoratorTransformsFirst() {
        var store = new InMemoryDataSource();
        compression(base64(store)).write(TEXT);                 // compress first, then encode: store holds Base64
        assertThat(new String(store.read(), StandardCharsets.US_ASCII)).matches("[A-Za-z0-9+/]+=*");

        base64(compression(store)).write(TEXT);                 // encode first, then compress: store holds GZIP
        byte[] stored = store.read();
        assertThat(stored[0]).isEqualTo((byte) 0x1f);
        assertThat(stored[1]).isEqualTo((byte) 0x8b);
    }

    @Test
    void sameDecoratorCanBeStackedTwice() {
        var store = new InMemoryDataSource();
        DataSource twice = base64(base64(store));
        twice.write(TEXT);
        byte[] once = Base64.getEncoder().encode(TEXT);
        assertThat(store.read()).isEqualTo(Base64.getEncoder().encode(once));
        assertThat(twice.read()).isEqualTo(TEXT);

        DataSource doubleCompressed = compression(compression(new InMemoryDataSource()));
        doubleCompressed.write(repetitive());
        assertThat(doubleCompressed.read()).isEqualTo(repetitive());
    }

    @Test
    void emptyDataRoundTrips() {
        var compressedStore = new InMemoryDataSource();
        var encodedStore = new InMemoryDataSource();
        DataSource compressed = compression(compressedStore);
        DataSource encoded = base64(encodedStore);
        compressed.write(new byte[0]);
        encoded.write(new byte[0]);
        assertThat(compressedStore.read()).isEmpty();
        assertThat(encodedStore.read()).isEmpty();
        assertThat(compressed.read()).isEmpty();
        assertThat(encoded.read()).isEmpty();
    }

    @Test
    void readingANeverWrittenSourceReturnsEmpty() {
        assertThat(compression(new InMemoryDataSource()).read()).isEmpty();
        assertThat(base64(new InMemoryDataSource()).read()).isEmpty();
        assertThat(base64(compression(new InMemoryDataSource())).read()).isEmpty();
    }

    @Test
    void corruptDataIsReportedAsIllegalState() {
        var notGzip = new InMemoryDataSource();
        notGzip.write("this is not gzip".getBytes(StandardCharsets.US_ASCII));
        assertThatIllegalStateException().isThrownBy(() -> compression(notGzip).read())
                .withCauseInstanceOf(IOException.class);

        var notBase64 = new InMemoryDataSource();
        notBase64.write("*** not base64 ***".getBytes(StandardCharsets.US_ASCII));
        assertThatIllegalStateException().isThrownBy(() -> base64(notBase64).read())
                .withCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void decoratorsWorkWithAnyDataSource() {
        // A test-only data source that keeps every version ever written.
        final class VersionedDataSource implements DataSource {
            final List<byte[]> versions = new ArrayList<>();

            @Override
            public void write(byte[] data) {
                versions.add(data.clone());
            }

            @Override
            public byte[] read() {
                return versions.isEmpty() ? new byte[0] : versions.getLast().clone();
            }
        }
        var versioned = new VersionedDataSource();
        DataSource source = base64(compression(versioned));
        source.write("v1".getBytes(StandardCharsets.US_ASCII));
        source.write(TEXT);
        assertThat(versioned.versions).hasSize(2);
        assertThat(source.read()).isEqualTo(TEXT);
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> compression(null));
        assertThatNullPointerException().isThrownBy(() -> base64(null));
        assertThatNullPointerException().isThrownBy(() -> compression(new InMemoryDataSource()).write(null));
        assertThatNullPointerException().isThrownBy(() -> base64(new InMemoryDataSource()).write(null));
    }
}
