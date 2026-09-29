package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The client: archives a report and announces it, using one provider family it receives in the constructor.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public final class ReportArchiver {

    private final BlobStorage storage;
    private final MessageQueue queue;

    public ReportArchiver(CloudFactory cloud) {
        Objects.requireNonNull(cloud, "cloud");
        this.storage = cloud.storage();
        this.queue = cloud.queue();
    }

    /** Stores the report under {@code reports/<name>}, publishes a notification and returns the URI. */
    public String archive(String name, String content) {
        String uri = storage.put("reports/" + name, content);
        queue.publish("archived " + uri);
        return uri;
    }

    public Optional<String> read(String uri) {
        return storage.get(uri);
    }

    public List<String> notifications() {
        return queue.messages();
    }
}
