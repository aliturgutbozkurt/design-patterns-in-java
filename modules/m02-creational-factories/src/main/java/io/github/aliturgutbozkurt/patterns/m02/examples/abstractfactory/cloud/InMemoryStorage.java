package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Simulated storage shared by the fictional providers; each provider configures its own scheme and region. */
final class InMemoryStorage implements BlobStorage {

    private final String provider;
    private final String uriPrefix;
    private final Map<String, String> objects = new LinkedHashMap<>();

    InMemoryStorage(String provider, String scheme, String region) {
        this.provider = provider;
        this.uriPrefix = scheme + "://" + region + "/";
    }

    @Override
    public String provider() {
        return provider;
    }

    @Override
    public String put(String key, String content) {
        String uri = uriPrefix + key;
        objects.put(uri, content);
        return uri;
    }

    @Override
    public Optional<String> get(String uri) {
        return Optional.ofNullable(objects.get(uri));
    }
}
