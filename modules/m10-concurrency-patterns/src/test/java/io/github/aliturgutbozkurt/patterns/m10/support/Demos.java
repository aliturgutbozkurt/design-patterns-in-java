package io.github.aliturgutbozkurt.patterns.m10.support;

/** Test helper: captures the output of a demo whose {@code main} declares checked exceptions. */
public final class Demos {

    /** A {@code main} body that may throw, e.g. {@code () -> CrawlerDemo.main(new String[0])}. */
    @FunctionalInterface
    public interface Main {
        void run() throws Exception;
    }

    private Demos() {}

    /** Runs {@code main} and returns everything it printed; a thrown exception fails the test. */
    public static String output(Main main) {
        return Console.capture(() -> {
            try {
                main.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError("demo was interrupted", e);
            } catch (Exception e) {
                throw new AssertionError("demo failed", e);
            }
        });
    }
}
