# Assignment 01 — Data-Source Decorators (Compression + Base64)

> Module: m04-structural-wrappers · Difficulty: ★★☆ · Estimated time: 2 h

## Goal

An application stores documents in a `DataSource` — here an in-memory "file". Some deployments want the data
**compressed**, some want it **Base64-encoded** (because the store only accepts text), some want both. Instead of
writing one class per combination, write two stackable **decorators**: each transforms the data on the way in and
undoes the transformation on the way out. You will see that stacking order changes what ends up in the store — but
never what the client reads back.

## What you are given

- `exercises/ex01/DataSource.java` — `write(byte[])`, `read()` — **do not modify**
- `exercises/ex01/InMemoryDataSource.java` — the "file store": keeps a defensive copy of the last write; `read()`
  before any write returns an empty array — **do not modify**
- `exercises/ex01/DataSourceDecorator.java`, `CompressionDecorator.java`, `Base64Decorator.java` — your code goes here
  (`TODO(ex01)` markers)

## Tasks

1. `DataSourceDecorator` (abstract): holds the wrapped `DataSource` (null → `NullPointerException`) and delegates to
   it. It holds **no data of its own**, so any decorator can wrap any `DataSource` — including another decorator of
   the same kind.
2. `CompressionDecorator`: `write` GZIP-compresses (`java.util.zip`) and passes the result to the wrapped source;
   `read` reads from the wrapped source and decompresses.
3. `Base64Decorator`: `write` stores the standard Base64 text as US-ASCII bytes; `read` decodes it.
4. For both: empty data stays empty in both directions (an empty array read from the wrapped source is returned as
   empty, not decoded); corrupt data on `read` throws `IllegalStateException` with the original exception as its
   **cause**; `write(null)` throws `NullPointerException`.

## Acceptance criteria

- [ ] `roundTripsThroughCompression`
- [ ] `roundTripsThroughBase64`
- [ ] `roundTripsThroughBothInEitherOrder`
- [ ] `compressionShrinksRepetitiveData`
- [ ] `base64StoresOnlyBase64Characters`
- [ ] `outermostDecoratorTransformsFirst` — `compression(base64(store))` leaves Base64 text in the store;
  `base64(compression(store))` leaves GZIP bytes (they start with the magic number `0x1f 0x8b`)
- [ ] `sameDecoratorCanBeStackedTwice`
- [ ] `emptyDataRoundTrips`
- [ ] `readingANeverWrittenSourceReturnsEmpty`
- [ ] `corruptDataIsReportedAsIllegalState`
- [ ] `decoratorsWorkWithAnyDataSource`
- [ ] `rejectsNullArguments`

## Run the tests

```bash
./mvnw -pl modules/m04-structural-wrappers test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — put the shared part in the base class</summary>

Both decorators do the same thing around a different transformation: "encode, then write" and "read, then decode".
Let `DataSourceDecorator` implement `write`/`read` once and call two abstract methods, `encode(byte[])` and
`decode(byte[])`, that the subclasses supply. (That is a small Template Method inside a Decorator.) The empty-array
rule then lives in one place.

</details>

<details><summary>Hint 2 — GZIP and Base64 in the JDK</summary>

Compress with `new GZIPOutputStream(byteArrayOutputStream)` in try-with-resources — the GZIP trailer is written only
when the stream is **closed**. Decompress with `new GZIPInputStream(new ByteArrayInputStream(bytes)).readAllBytes()`.
Garbage throws `ZipException`, a truncated stream `EOFException` — both are `IOException`s. `Base64.getEncoder()
.encode(bytes)` already returns ASCII bytes; `Base64.getDecoder().decode(bytes)` throws `IllegalArgumentException` on
invalid input.

</details>

<details><summary>Hint 3 — why must empty stay empty?</summary>

GZIP of an empty array is 20 bytes, and reading an *empty* stream with `GZIPInputStream` throws `EOFException`. So a
never-written store (empty) must not be "decompressed", and writing nothing should store nothing.

</details>

## Stretch goals (optional, not graded)

- Add an `EncryptionDecorator` (AES from `javax.crypto`) and decide where it belongs in the stack: before or after
  compression? (Hint: encrypted data does not compress.)
- Write the same two transformations as `UnaryOperator<byte[]>` and compose them with `andThen`. What do you lose
  compared to decorators that implement `DataSource`?
