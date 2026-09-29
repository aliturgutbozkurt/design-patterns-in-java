package io.github.aliturgutbozkurt.patterns.m01.examples.isp.after;

import java.util.List;
import java.util.Objects;

/**
 * A client that needs only {@link DocumentScanner}.
 *
 * @see "m01 lesson, section ISP"
 */
public final class ArchiveService {

    private final DocumentScanner scanner;

    public ArchiveService(DocumentScanner scanner) {
        this.scanner = Objects.requireNonNull(scanner, "scanner");
    }

    public List<String> archive(List<String> pages) {
        return pages.stream().map(page -> "archived: " + scanner.scan(page)).toList();
    }
}
